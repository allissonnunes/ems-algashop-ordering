package com.github.allisson95.algashop.ordering.application.utility;

import lombok.Builder;

@Builder
public record PageFilter(int size, int page) {

}
