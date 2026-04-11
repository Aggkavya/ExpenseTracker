package com.personal.Expense_Tracker.DTO;

import com.personal.Expense_Tracker.entity.FriendRequestStatus;
import lombok.Data;

import java.util.Date;

@Data
public class FriendRequestResponse {
    private Long requestId;
    private String senderName;
    private String senderUserName;
    private String receiverName;
    private String receiverUserName;
    private FriendRequestStatus status;
    private Date date;
}
