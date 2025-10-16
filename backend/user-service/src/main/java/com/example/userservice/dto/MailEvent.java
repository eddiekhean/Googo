package com.example.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MailEvent {
    private String type;       // loại mail (enum)
    private Map<String, Object> data; // dữ liệu động kèm theo
    private String to;           // địa chỉ người nhận
}
