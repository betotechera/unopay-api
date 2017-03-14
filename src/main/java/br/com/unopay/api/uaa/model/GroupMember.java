package br.com.unopay.api.uaa.model;

import br.com.unopay.api.uaa.model.valistionsgroups.Create;
import br.com.unopay.api.uaa.model.valistionsgroups.Update;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
@Data
@Table(name = "oauth_group_members")
public class GroupMember implements Serializable{

    @Id
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy="uuid2")
    @NotNull(groups = Update.class)
    @Column(name="id")
    private String id;

    @NotNull(groups = Create.class)
    @Column(name="user_id", unique = true)
    private String member;

    @NotNull(groups = Create.class)
    @Column(name="group_id")
    private String group;

    public  GroupMember(){}

    public GroupMember(UserDetail user, Group group) {
        this.member = user.getId();
        this.group = group.getId();
    }
}
