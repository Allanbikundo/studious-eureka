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
@Table(
        name = "business",
        indexes = {@Index(name = "idx_business_tree_path", columnList = "treePath")})
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
    @org.hibernate.annotations.ColumnTransformer(write = "?::ltree")
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private String treePath;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private String settingsSnapshot;

    @Column(nullable = false)
    private boolean isActive = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "industry_id")
    private BusinessIndustry industry;

    @Enumerated(EnumType.STRING)
    private BusinessSize businessSize;

    private String website;

    private String location;

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
