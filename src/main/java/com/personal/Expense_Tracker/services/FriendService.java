package com.personal.Expense_Tracker.services;

import com.personal.Expense_Tracker.DTO.FriendRequestResponse;
import com.personal.Expense_Tracker.entity.FriendRequest;
import com.personal.Expense_Tracker.entity.FriendRequestStatus;
import com.personal.Expense_Tracker.entity.User;
import com.personal.Expense_Tracker.repositry.FriendRepository;
import com.personal.Expense_Tracker.repositry.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FriendService {

    @Autowired
    private FriendRepository friendRepository;

    @Autowired
    private UserRepository userRepository;

    // ── Helper ────────────────────────────────────────────────────────────────

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUserName(username);
    }

    private FriendRequestResponse toDTO(FriendRequest f) {
        FriendRequestResponse dto = new FriendRequestResponse();
        dto.setRequestId(f.getId());
        dto.setSenderName(f.getSender().getName());
        dto.setSenderUserName(f.getSender().getUserName());
        dto.setReceiverName(f.getReceiver().getName());
        dto.setReceiverUserName(f.getReceiver().getUserName());
        dto.setStatus(f.getFriendRequestStatus());
        dto.setDate(f.getDate());
        return dto;
    }

    // ── 1. Send Friend Request ────────────────────────────────────────────────

    public FriendRequestResponse sendFriendRequest(String receiverUsername) {
        User sender = getCurrentUser();
        User receiver = userRepository.findByUserName(receiverUsername);

        // Validation 1: receiver must exist
        if (receiver == null) {
            throw new RuntimeException("User not found: " + receiverUsername);
        }

        // Validation 2: can't send to yourself
        if (sender.getId().equals(receiver.getId())) {
            throw new RuntimeException("You cannot send a friend request to yourself");
        }

        // Validation 3: check if any request already exists in EITHER direction
        Optional<FriendRequest> existing = friendRepository
                .findExistingRequest(sender.getId(), receiver.getId());

        if (existing.isPresent()) {
            FriendRequest existingReq = existing.get();
            FriendRequestStatus currentStatus = existingReq.getFriendRequestStatus();

            if (currentStatus == FriendRequestStatus.PENDING) {
                throw new RuntimeException("A friend request is already pending between you two");
            } else if (currentStatus == FriendRequestStatus.ACCEPTED) {
                throw new RuntimeException("You are already friends with " + receiverUsername);
            } else if (currentStatus == FriendRequestStatus.REJECTED) {
                // Allow re-sending if previously rejected — reuse the existing row
                log.info("Re-sending previously rejected friend request from {} to {}",
                        sender.getUserName(), receiverUsername);
                existingReq.setFriendRequestStatus(FriendRequestStatus.PENDING);
                existingReq.setSender(sender);    // reset direction to current sender
                existingReq.setReceiver(receiver);
                existingReq.setDate(new Date());
                friendRepository.save(existingReq);
                return toDTO(existingReq);
            }
        }

        // Create new request
        FriendRequest newRequest = FriendRequest.builder()
                .sender(sender)
                .receiver(receiver)
                .friendRequestStatus(FriendRequestStatus.PENDING)
                .date(new Date())
                .build();

        friendRepository.save(newRequest);
        log.info("Friend request sent from {} to {}", sender.getUserName(), receiverUsername);
        return toDTO(newRequest);
    }

    // ── 2. My Pending Inbox (requests others sent TO me) ─────────────────────

    public List<FriendRequestResponse> getPendingRequests() {
        User me = getCurrentUser();
        List<FriendRequest> pending = friendRepository
                .findByReceiverIdAndFriendRequestStatus(me.getId(), FriendRequestStatus.PENDING);
        log.info("User {} has {} pending friend requests", me.getUserName(), pending.size());
        return pending.stream().map(this::toDTO).collect(Collectors.toList());
    }

    // ── 3. My Sent Requests (still waiting) ──────────────────────────────────

    public List<FriendRequestResponse> getSentRequests() {
        User me = getCurrentUser();
        return friendRepository
                .findBySenderIdAndFriendRequestStatus(me.getId(), FriendRequestStatus.PENDING)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    // ── 4. Accept a Friend Request ───────────────────────────────────────────

    public FriendRequestResponse acceptRequest(Long requestId) {
        User me = getCurrentUser();
        FriendRequest request = friendRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Friend request not found"));

        // Only the RECEIVER can accept
        if (!request.getReceiver().getId().equals(me.getId())) {
            throw new RuntimeException("You are not authorized to accept this request");
        }

        // Can only accept PENDING requests
        if (request.getFriendRequestStatus() != FriendRequestStatus.PENDING) {
            throw new RuntimeException("This request is already " + request.getFriendRequestStatus());
        }

        request.setFriendRequestStatus(FriendRequestStatus.ACCEPTED);
        friendRepository.save(request);
        log.info("Friend request {} accepted by {}", requestId, me.getUserName());
        return toDTO(request);
    }

    // ── 5. Reject a Friend Request ───────────────────────────────────────────

    public FriendRequestResponse rejectRequest(Long requestId) {
        User me = getCurrentUser();
        FriendRequest request = friendRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Friend request not found"));

        // Only the RECEIVER can reject
        if (!request.getReceiver().getId().equals(me.getId())) {
            throw new RuntimeException("You are not authorized to reject this request");
        }

        // Can only reject PENDING requests
        if (request.getFriendRequestStatus() != FriendRequestStatus.PENDING) {
            throw new RuntimeException("This request is already " + request.getFriendRequestStatus());
        }

        request.setFriendRequestStatus(FriendRequestStatus.REJECTED);
        friendRepository.save(request);
        log.info("Friend request {} rejected by {}", requestId, me.getUserName());
        return toDTO(request);
    }

    // ── 6. Get All Friends ───────────────────────────────────────────────────

    public List<FriendRequestResponse> getAllFriends() {
        User me = getCurrentUser();
        return friendRepository
                .findAllFriendsByUserId(me.getId(), FriendRequestStatus.ACCEPTED)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    // ── 7. Unfriend ──────────────────────────────────────────────────────────

    public String unfriend(Long requestId) {
        User me = getCurrentUser();
        FriendRequest request = friendRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Friendship not found"));

        // Only sender or receiver can unfriend
        boolean isSender = request.getSender().getId().equals(me.getId());
        boolean isReceiver = request.getReceiver().getId().equals(me.getId());
        if (!isSender && !isReceiver) {
            throw new RuntimeException("You are not part of this friendship");
        }

        if (request.getFriendRequestStatus() != FriendRequestStatus.ACCEPTED) {
            throw new RuntimeException("You are not friends with this user");
        }

        friendRepository.deleteById(requestId);
        log.info("User {} unfriended request {}", me.getUserName(), requestId);
        return "Unfriended successfully";
    }
}
