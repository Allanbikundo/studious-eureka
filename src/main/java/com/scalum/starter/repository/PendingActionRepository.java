package com.scalum.starter.repository;

import com.scalum.starter.model.ActionStatus;
import com.scalum.starter.model.PendingAction;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PendingActionRepository extends JpaRepository<PendingAction, Long> {

    List<PendingAction> findByTargetBusinessIdAndStatus(UUID targetBusinessId, ActionStatus status);

    List<PendingAction> findByRequesterUserId(UUID requesterUserId);
}
