package com.alenaco.mytranslator.main.model;

import org.apache.commons.lang.ObjectUtils;

import javax.xml.bind.annotation.XmlAttribute;
import java.util.UUID;

/**
 * @author kovalenko
 * @version $Id$
 */
public class UUIDEntity {
    private UUID id;

    public UUIDEntity() {
        this.id = UUID.randomUUID();
    }

    public UUIDEntity(UUID id) {
        this.id = id;
    }

    @XmlAttribute()
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof UUIDEntity) && ObjectUtils.equals(((UUIDEntity) obj).getId(), this.id);
    }
}
