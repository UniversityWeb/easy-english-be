package com.universityweb.notification.request;

import com.universityweb.common.request.ListRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class GetNotificationsRequest extends ListRequest {
    private String username;

    public GetNotificationsRequest(int page, int size, String username) {
        super(page, size);
        this.username = username;
    }
}
