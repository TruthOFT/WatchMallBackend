package com.watch.watch_mall.model.inner_data;

import lombok.Data;

import java.io.Serializable;

@Data
public class FeatureItem implements Serializable {
    private static final long serialVersionUID = 1L;

    String label;
    String value;
}
