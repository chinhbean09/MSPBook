package com.chinhbean.chat.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
/// không chỉ lưu userId mà cả username, avatar để tiện query nhanh, tránh phải gọi thêm API
// Được embed trực tiếp vào conversation thay vì chỉ lưu userId
public class ParticipantInfo {
    String userId;
    String username;
    String firstName;
    String lastName;
    String avatar;
}
