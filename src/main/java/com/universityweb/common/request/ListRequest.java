package com.universityweb.common.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public abstract class ListRequest {
    @Schema(description = "The page number to retrieve (0-based index).", example = "0")
    protected int page;

    @Schema(description = "The size of the page (number of items per page).", example = "10")
    protected int size;
}
