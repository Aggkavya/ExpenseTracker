package com.personal.Expense_Tracker.entity;

public enum NotificationType {
    FRIEND_REQUEST,        // Someone sent you a friend request
    FRIEND_ACCEPTED,       // Someone accepted your friend request
    FRIEND_REJECTED,       // Someone rejected your friend request
    LINKED_TXN_REQUEST,    // A friend wants to create a shared transaction
    LINKED_TXN_ACCEPTED,   // Your transaction request was accepted
    LINKED_TXN_REJECTED    // Your transaction request was rejected
}
