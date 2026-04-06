package com.watch.watch_mall.service.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.mapping.Property;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.watch.watch_mall.common.ErrorCode;
import com.watch.watch_mall.config.SearchProperties;
import com.watch.watch_mall.exception.BusinessException;
import com.watch.watch_mall.exception.ThrowUtils;
import com.watch.watch_mall.mapper.ProductMapper;
import com.watch.watch_mall.model.es.ProductSearchDocument;
import com.watch.watch_mall.model.vo.ProductSearchIndexVO;
import com.watch.watch_mall.model.vo.ProductVO;
import com.watch.watch_mall.service.ProductSearchService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class ProductSearchServiceImpl implements ProductSearchService {

    private static final long MAX_PAGE_SIZE = 50;

    private final ElasticsearchClient elasticsearchClient;
    private final SearchProperties searchProperties;
    private final ProductMapper productMapper;

    public ProductSearchServiceImpl(ElasticsearchClient elasticsearchClient,
                                    SearchProperties searchProperties,
                                    ProductMapper productMapper) {
        this.elasticsearchClient = elasticsearchClient;
        this.searchProperties = searchProperties;
        this.productMapper = productMapper;
    }

    @Override
    public void syncProductById(Long productId) {
        if (productId == null || productId <= 0) {
            return;
        }
        ProductSearchIndexVO indexVO = productMapper.getSearchProductById(productId);
        if (indexVO == null) {
            deleteProductById(productId);
            return;
        }

        ensureIndexExists();
        ProductSearchDocument document = toDocument(indexVO);
        try {
            elasticsearchClient.index(index -> index
                    .index(searchProperties.getProductIndex())
                    .id(String.valueOf(productId))
                    .document(document));
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "sync product to elasticsearch failed");
        }
    }

    @Override
    public void deleteProductById(Long productId) {
        if (productId == null || productId <= 0) {
            return;
        }
        try {
            if (!indexExists()) {
                return;
            }
            elasticsearchClient.delete(delete -> delete
                    .index(searchProperties.getProductIndex())
                    .id(String.valueOf(productId)));
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "delete product from elasticsearch failed");
        }
    }

    @Override
    public long rebuildProductIndex() {
        recreateIndex();
        List<ProductSearchIndexVO> productList = productMapper.listSearchProducts();
        if (productList == null || productList.isEmpty()) {
            return 0L;
        }

        try {
            BulkRequest.Builder bulkBuilder = new BulkRequest.Builder();
            for (ProductSearchIndexVO product : productList) {
                ProductSearchDocument document = toDocument(product);
                bulkBuilder.operations(operation -> operation.index(index -> index
                        .index(searchProperties.getProductIndex())
                        .id(String.valueOf(product.getId()))
                        .document(document)));
            }
            elasticsearchClient.bulk(bulkBuilder.build());
            return productList.size();
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "rebuild elasticsearch product index failed");
        }
    }

    @Override
    public Page<ProductVO> searchProducts(String keyword, long current, long pageSize) {
        ThrowUtils.throwIf(StringUtils.isBlank(keyword), ErrorCode.PARAMS_ERROR, "keyword must not be blank");
        ThrowUtils.throwIf(current <= 0 || pageSize <= 0 || pageSize > MAX_PAGE_SIZE, ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(keyword.trim().length() < 2, ErrorCode.PARAMS_ERROR, "keyword length must be at least 2");

        if (!indexExists()) {
            return new Page<>(current, pageSize, 0);
        }

        try {
            SearchResponse<ProductSearchDocument> response = elasticsearchClient.search(search -> search
                            .index(searchProperties.getProductIndex())
                            .from((int) ((current - 1) * pageSize))
                            .size((int) pageSize)
                            .query(buildSearchQuery(keyword.trim()))
                            .sort(sort -> sort.score(score -> score.order(SortOrder.Desc)))
                            .sort(sort -> sort.field(field -> field.field("updateTime").order(SortOrder.Desc))),
                    ProductSearchDocument.class);

            List<ProductVO> records = new ArrayList<>();
            for (Hit<ProductSearchDocument> hit : response.hits().hits()) {
                ProductSearchDocument source = hit.source();
                if (source == null) {
                    continue;
                }
                records.add(toProductVO(source));
            }

            long total = response.hits().total() == null ? records.size() : response.hits().total().value();
            Page<ProductVO> page = new Page<>(current, pageSize, total);
            page.setRecords(records);
            return page;
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "search elasticsearch products failed");
        }
    }

    private Query buildSearchQuery(String keyword) {
        return Query.of(query -> query.bool(bool -> bool
                .should(should -> should.match(match -> match.field("name").query(keyword).boost(6.0f)))
                .should(should -> should.match(match -> match.field("name.prefix").query(keyword).boost(4.0f)))
                .should(should -> should.match(match -> match.field("name.ngram").query(keyword).boost(2.0f)))
                .minimumShouldMatch("1")
                .filter(filter -> filter.term(term -> term.field("status").value(FieldValue.of(1))))));
    }

    private ProductVO toProductVO(ProductSearchDocument document) {
        ProductVO productVO = new ProductVO();
        productVO.setId(document.getId());
        productVO.setName(document.getName());
        productVO.setTitle(document.getTitle());
        productVO.setDescription(document.getDescription());
        productVO.setTags(document.getTags());
        productVO.setPrice(document.getPrice());
        productVO.setStatus(document.getStatus());
        productVO.setUrl(document.getMainImageUrl());
        return productVO;
    }

    private ProductSearchDocument toDocument(ProductSearchIndexVO product) {
        ProductSearchDocument document = new ProductSearchDocument();
        document.setId(product.getId());
        document.setName(product.getName());
        document.setTitle(product.getTitle());
        document.setTags(product.getTags());
        document.setDescription(product.getDescription());
        document.setPrice(product.getPrice());
        document.setStatus(product.getStatus());
        document.setMainImageUrl(product.getMainImageUrl());
        document.setCategoryNames(product.getCategoryNames());
        document.setUpdateTime(product.getUpdateTime());
        return document;
    }

    private void ensureIndexExists() {
        if (indexExists()) {
            return;
        }
        createIndex();
    }

    private boolean indexExists() {
        try {
            return elasticsearchClient.indices()
                    .exists(request -> request.index(searchProperties.getProductIndex()))
                    .value();
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "check elasticsearch index failed");
        }
    }

    private void recreateIndex() {
        try {
            if (indexExists()) {
                elasticsearchClient.indices().delete(request -> request.index(searchProperties.getProductIndex()));
            }
            createIndex();
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "recreate elasticsearch index failed");
        }
    }

    private void createIndex() {
        try {
            elasticsearchClient.indices().create(create -> create
                    .index(searchProperties.getProductIndex())
                    .settings(settings -> settings
                            .maxNgramDiff(18)
                            .analysis(analysis -> analysis
                                    .filter("prefix_edge_ngram", filter -> filter
                                            .definition(definition -> definition.edgeNgram(edgeNgram -> edgeNgram
                                                    .minGram(2)
                                                    .maxGram(20))))
                                    .filter("contains_ngram", filter -> filter
                                            .definition(definition -> definition.ngram(ngram -> ngram
                                                    .minGram(2)
                                                    .maxGram(20))))
                                    .analyzer("prefix_analyzer", analyzer -> analyzer
                                            .custom(custom -> custom
                                                    .tokenizer("standard")
                                                    .filter("lowercase", "prefix_edge_ngram")))
                                    .analyzer("contains_analyzer", analyzer -> analyzer
                                            .custom(custom -> custom
                                                    .tokenizer("standard")
                                                    .filter("lowercase", "contains_ngram")))
                                    .analyzer("lowercase_analyzer", analyzer -> analyzer
                                            .custom(custom -> custom
                                                    .tokenizer("standard")
                                                    .filter("lowercase")))))
                    .mappings(mapping -> mapping
                            .properties("id", Property.of(property -> property.long_(number -> number)))
                            .properties("name", Property.of(property -> property.text(text -> text
                                    .analyzer("lowercase_analyzer")
                                    .searchAnalyzer("lowercase_analyzer")
                                    .fields("prefix", field -> field.text(prefix -> prefix
                                            .analyzer("prefix_analyzer")
                                            .searchAnalyzer("lowercase_analyzer")))
                                    .fields("ngram", field -> field.text(ngram -> ngram
                                            .analyzer("contains_analyzer")
                                            .searchAnalyzer("lowercase_analyzer")))
                                    .fields("keyword", field -> field.keyword(keyword -> keyword)))))
                            .properties("title", Property.of(property -> property.text(text -> text)))
                            .properties("tags", Property.of(property -> property.text(text -> text)))
                            .properties("description", Property.of(property -> property.text(text -> text)))
                            .properties("price", Property.of(property -> property.double_(number -> number)))
                            .properties("status", Property.of(property -> property.integer(integer -> integer)))
                            .properties("mainImageUrl", Property.of(property -> property.keyword(keyword -> keyword)))
                            .properties("categoryNames", Property.of(property -> property.text(text -> text)))
                            .properties("updateTime", Property.of(property -> property.date(date -> date)))));
        } catch (IOException e) {
            log.error("create elasticsearch index failed", e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "create elasticsearch product index failed");
        }
    }
}
