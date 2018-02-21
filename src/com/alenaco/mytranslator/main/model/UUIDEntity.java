package com.alenaco.mytranslator.main.model;

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

    @XmlAttribute()
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}
