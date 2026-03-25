/*
 Navicat Premium Dump SQL

 Source Server         : MyDB
 Source Server Type    : MySQL
 Source Server Version : 80031 (8.0.31)
 Source Host           : localhost:3306
 Source Schema         : watch_mall

 Target Server Type    : MySQL
 Target Server Version : 80031 (8.0.31)
 File Encoding         : 65001

 Date: 17/02/2026 22:51:50
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for attribute_values
-- ----------------------------
DROP TABLE IF EXISTS `attribute_values`;
CREATE TABLE `attribute_values`  (
                                     `id` bigint UNSIGNED NOT NULL COMMENT 'ID (雪花算法)',
                                     `attributeId` bigint UNSIGNED NOT NULL,
                                     `value` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
                                     PRIMARY KEY (`id`) USING BTREE,
                                     INDEX `fk_av_attr`(`attributeId` ASC) USING BTREE,
                                     CONSTRAINT `fk_av_attr` FOREIGN KEY (`attributeId`) REFERENCES `attributes` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for attributes
-- ----------------------------
DROP TABLE IF EXISTS `attributes`;
CREATE TABLE `attributes`  (
                               `id` bigint UNSIGNED NOT NULL COMMENT 'ID (雪花算法)',
                               `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
                               `categoryId` bigint UNSIGNED NULL DEFAULT NULL,
                               `createTime` datetime NULL DEFAULT CURRENT_TIMESTAMP,
                               PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for category
-- ----------------------------
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category`  (
                             `id` bigint UNSIGNED NOT NULL COMMENT '分类ID (雪花算法)',
                             `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
                             `parentId` bigint UNSIGNED NULL DEFAULT 0 COMMENT '父ID',
                             `icon` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
                             `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
                             `sortOrder` int NULL DEFAULT 0,
                             `isShow` tinyint UNSIGNED NULL DEFAULT 1,
                             `createTime` datetime NULL DEFAULT CURRENT_TIMESTAMP,
                             `updateTime` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                             `isDelete` tinyint UNSIGNED NULL DEFAULT 0,
                             PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '商品分类表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for product
-- ----------------------------
DROP TABLE IF EXISTS `product`;
CREATE TABLE `product`  (
                            `id` bigint UNSIGNED NOT NULL COMMENT '商品ID (雪花算法)',
                            `name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
                            `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
                            `brandId` bigint UNSIGNED NULL DEFAULT NULL,
                            `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
                            `feature` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'JSON规格',
                            `tags` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
                            `price` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '因源数据缺失，默认为0.00',
                            `isHero` tinyint UNSIGNED NULL DEFAULT 0,
                            `isBanner` tinyint UNSIGNED NULL DEFAULT 0,
                            `isRec` tinyint UNSIGNED NULL DEFAULT 0,
                            `status` tinyint UNSIGNED NULL DEFAULT 1,
                            `version` int UNSIGNED NULL DEFAULT 1 COMMENT '乐观锁',
                            `createTime` datetime NULL DEFAULT CURRENT_TIMESTAMP,
                            `updateTime` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                            `isDelete` tinyint UNSIGNED NULL DEFAULT 0,
                            PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '商品主表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for product_category
-- ----------------------------
DROP TABLE IF EXISTS `product_category`;
CREATE TABLE `product_category`  (
                                     `productId` bigint UNSIGNED NOT NULL,
                                     `categoryId` bigint UNSIGNED NOT NULL,
                                     PRIMARY KEY (`productId`, `categoryId`) USING BTREE,
                                     INDEX `fk_pc_category`(`categoryId` ASC) USING BTREE,
                                     CONSTRAINT `fk_pc_category` FOREIGN KEY (`categoryId`) REFERENCES `category` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
                                     CONSTRAINT `fk_pc_product` FOREIGN KEY (`productId`) REFERENCES `product` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '商品分类关联' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for product_images
-- ----------------------------
DROP TABLE IF EXISTS `product_images`;
CREATE TABLE `product_images`  (
                                   `id` bigint UNSIGNED NOT NULL COMMENT '图片ID (雪花算法)',
                                   `productId` bigint UNSIGNED NOT NULL,
                                   `url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
                                   `isMain` tinyint UNSIGNED NULL DEFAULT 0,
                                   `sortOrder` int NULL DEFAULT 0,
                                   `createTime` datetime NULL DEFAULT CURRENT_TIMESTAMP,
                                   PRIMARY KEY (`id`) USING BTREE,
                                   INDEX `idx_pi_product`(`productId` ASC) USING BTREE,
                                   CONSTRAINT `fk_pi_product` FOREIGN KEY (`productId`) REFERENCES `product` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '商品图片表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for product_skus
-- ----------------------------
DROP TABLE IF EXISTS `product_skus`;
CREATE TABLE `product_skus`  (
                                 `id` bigint UNSIGNED NOT NULL COMMENT 'SKU ID (雪花算法)',
                                 `productId` bigint UNSIGNED NOT NULL,
                                 `skuCode` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
                                 `skuName` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
                                 `image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
                                 `price` decimal(10, 2) NOT NULL,
                                 `marketPrice` decimal(10, 2) NULL DEFAULT 0.00,
                                 `stock` int UNSIGNED NOT NULL DEFAULT 0,
                                 `lockStock` int UNSIGNED NULL DEFAULT 0,
                                 `version` int UNSIGNED NULL DEFAULT 1 COMMENT '乐观锁',
                                 `createTime` datetime NULL DEFAULT CURRENT_TIMESTAMP,
                                 `updateTime` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                 PRIMARY KEY (`id`) USING BTREE,
                                 UNIQUE INDEX `uk_sku_code`(`skuCode` ASC) USING BTREE,
                                 INDEX `idx_ps_product`(`productId` ASC) USING BTREE,
                                 CONSTRAINT `fk_ps_product` FOREIGN KEY (`productId`) REFERENCES `product` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '商品SKU表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sku_attribute_mapping
-- ----------------------------
DROP TABLE IF EXISTS `sku_attribute_mapping`;
CREATE TABLE `sku_attribute_mapping`  (
                                          `skuId` bigint UNSIGNED NOT NULL,
                                          `attributeValueId` bigint UNSIGNED NOT NULL,
                                          PRIMARY KEY (`skuId`, `attributeValueId`) USING BTREE,
                                          INDEX `fk_sam_val`(`attributeValueId` ASC) USING BTREE,
                                          CONSTRAINT `fk_sam_sku` FOREIGN KEY (`skuId`) REFERENCES `product_skus` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
                                          CONSTRAINT `fk_sam_val` FOREIGN KEY (`attributeValueId`) REFERENCES `attribute_values` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
                         `id` bigint NOT NULL,
                         `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                         `userAccount` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                         `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                         `userPassword` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                         `avatarUrl` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                         `gender` tinyint NULL DEFAULT 0,
                         `phone` varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                         `balance` decimal(10, 2) NULL DEFAULT 0.00,
                         `createTime` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
                         `updateTime` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                         `isDelete` tinyint NULL DEFAULT 0,
                         `userRole` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'user',
                         PRIMARY KEY (`id`) USING BTREE,
                         UNIQUE INDEX `userAccount`(`userAccount` ASC) USING BTREE,
                         UNIQUE INDEX `email`(`email` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

SET FOREIGN_KEY_CHECKS = 1;
