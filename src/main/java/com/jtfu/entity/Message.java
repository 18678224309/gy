package com.jtfu.entity;

import java.time.LocalDateTime;
import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author jtfu
 * @since 2019-10-22
 */
public class Message implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    private Integer initiator;

    private Integer recipient;

    private String message;

    private LocalDateTime createtime;

    private LocalDateTime updatetime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public Integer getInitiator() {
        return initiator;
    }

    public void setInitiator(Integer initiator) {
        this.initiator = initiator;
    }
    public Integer getRecipient() {
        return recipient;
    }

    public void setRecipient(Integer recipient) {
        this.recipient = recipient;
    }
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    public LocalDateTime getCreatetime() {
        return createtime;
    }

    public void setCreatetime(LocalDateTime createtime) {
        this.createtime = createtime;
    }
    public LocalDateTime getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(LocalDateTime updatetime) {
        this.updatetime = updatetime;
    }

    @Override
    public String toString() {
        return "Message{" +
            "id=" + id +
            ", initiator=" + initiator +
            ", recipient=" + recipient +
            ", message=" + message +
            ", createtime=" + createtime +
            ", updatetime=" + updatetime +
        "}";
    }
}
