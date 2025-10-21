package com.umutyenidil.atlas.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PageResponse<T> {

    private List<T> content;      // list of items on current page
    private int pageNumber;       // current page index
    private int pageSize;         // size of page
    private long totalElements;   // total items in DB
    private int totalPages;       // total pages
    private boolean last;         // is last page
}