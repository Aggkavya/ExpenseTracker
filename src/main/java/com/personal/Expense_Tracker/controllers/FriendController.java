package com.personal.Expense_Tracker.controllers;

import com.personal.Expense_Tracker.DTO.FriendRequestResponse;
import com.personal.Expense_Tracker.services.FriendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/friend")
public class FriendController {

    @Autowired
    private FriendService friendService;

    // POST /friend/sendRequest
    @PostMapping("/sendRequest/{userName}")
    public ResponseEntity<FriendRequestResponse> sendFriendRequest(@PathVariable String userName) {
        FriendRequestResponse response = friendService.sendFriendRequest(userName);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // GET /friend/pending — my inbox (requests sent TO me)
    @GetMapping("/pending")
    public ResponseEntity<List<FriendRequestResponse>> getPendingRequests() {
        List<FriendRequestResponse> pending = friendService.getPendingRequests();
        return new ResponseEntity<>(pending, HttpStatus.OK);
    }

    // GET /friend/sent — requests I sent that are still waiting
    @GetMapping("/sent")
    public ResponseEntity<List<FriendRequestResponse>> getSentRequests() {
        List<FriendRequestResponse> sent = friendService.getSentRequests();
        return new ResponseEntity<>(sent, HttpStatus.OK);
    }

    // POST /friend/accept/1 — accept request with id=1
    @PostMapping("/accept/{requestId}")
    public ResponseEntity<FriendRequestResponse> acceptRequest(@PathVariable Long requestId) {
        FriendRequestResponse response = friendService.acceptRequest(requestId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // POST /friend/reject/1 — reject request with id=1
    @PostMapping("/reject/{requestId}")
    public ResponseEntity<FriendRequestResponse> rejectRequest(@PathVariable Long requestId) {
        FriendRequestResponse response = friendService.rejectRequest(requestId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // GET /friend/all — all accepted friends
    @GetMapping("/all")
    public ResponseEntity<List<FriendRequestResponse>> getAllFriends() {
        List<FriendRequestResponse> friends = friendService.getAllFriends();
        return new ResponseEntity<>(friends, HttpStatus.OK);
    }

    // DELETE /friend/unfriend/1 — remove friendship
    @DeleteMapping("/unfriend/{requestId}")
    public ResponseEntity<String> unfriend(@PathVariable Long requestId) {
        String message = friendService.unfriend(requestId);
        return new ResponseEntity<>(message, HttpStatus.OK);
    }
}
