package com.scalum.starter.model;

import jakarta.persistence.*;
import java.util.UUID;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@Table(name = "business", indexes = {
    @Index(name = "idx_business_tree_path", columnList = "treePath")
})
public class Business extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "parent_id")
    private UUID parentId;

    @Column(nullable = false)
    private String businessName;

    private String taxId;

    @Column(nullable = false)
    private UUID createdByUserId;

    @Column(columnDefinition = "ltree")
    @JdbcTypeCode(SqlTypes.VARCHAR) // Treat as VARCHAR on Java side, but cast to ltree on DB side? No, this just maps to VARCHAR.
    // To handle ltree properly with Hibernate 6, we usually need a custom type or rely on implicit casting if the driver supports it.
    // However, the error says "expression is of type character varying".
    // We need to tell Hibernate to cast it.
    // Or we can use @ColumnTransformer.
    @org.hibernate.annotations.ColumnTransformer(write = "?::ltree")
    private String treePath;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private String settingsSnapshot;

    @Column(nullable = false)
    private boolean isActive = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", insertable = false, updatable = false)
    private Business parent;

    public boolean isDescendantOf(UUID ancestorId) {
        if (treePath == null || ancestorId == null) {
            return false;
        }
        // Assuming treePath is dot-separated UUIDs or similar ltree format
        return treePath.contains(ancestorId.toString());
    }

    public Business getRootBusiness() {
        if (parent == null) {
            return this;
        }
        return parent.getRootBusiness();
    }
}
