package br.dev.allissonnunes.algashop.ordering.core.application.utility;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageFilter {

    private int page = 0;

    private int size = 15;

}
