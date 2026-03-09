package com.personal.Expense_Tracker.DTO;

import jakarta.persistence.Column;
import lombok.Data;

@Data
public class CreateNewUserRequest {
    private String name;

    private String userName;

    private String email;

    private String password;

}
