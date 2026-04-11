package com.personal.Expense_Tracker.repositry;

import com.personal.Expense_Tracker.entity.FriendRequest;
import com.personal.Expense_Tracker.entity.FriendRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendRepository extends JpaRepository<FriendRequest, Long> {

    // Bidirectional duplicate check — did A→B OR B→A request already exist?
    @Query("SELECT f FROM FriendRequest f WHERE " +
           "(f.sender.id = :senderId AND f.receiver.id = :receiverId) OR " +
           "(f.sender.id = :receiverId AND f.receiver.id = :senderId)")
    Optional<FriendRequest> findExistingRequest(@Param("senderId") Long senderId,
                                                @Param("receiverId") Long receiverId);

    // My inbox — pending requests others sent TO me
    List<FriendRequest> findByReceiverIdAndFriendRequestStatus(Long receiverId, FriendRequestStatus status);

    // Sent — pending requests I sent, still waiting
    List<FriendRequest> findBySenderIdAndFriendRequestStatus(Long senderId, FriendRequestStatus status);

    // All friends — ACCEPTED in either direction (I am sender OR receiver)
    @Query("SELECT f FROM FriendRequest f WHERE " +
           "(f.sender.id = :userId OR f.receiver.id = :userId) " +
           "AND f.friendRequestStatus = :status")
    List<FriendRequest> findAllFriendsByUserId(@Param("userId") Long userId,
                                               @Param("status") FriendRequestStatus status);
}
