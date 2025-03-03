package com.lb.book_worm_api.dto;

import java.util.List;

public interface PersonProjection {
    Long getId();
    String getFirstName();
    String getPrefix();
    String getLastName();
    String getRoles(); // ✅ Fetch roles as a comma-separated string
}
