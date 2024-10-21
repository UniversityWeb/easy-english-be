package com.universityweb.common.request;

import lombok.AllArgsConstructor;
import lombok.*;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class GetByUsernameRequest extends ListRequest {
    private String username;

    public GetByUsernameRequest(int page, int size, String username) {
        super(page, size);
        this.username = username;
    }
}
