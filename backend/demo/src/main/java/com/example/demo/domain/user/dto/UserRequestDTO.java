package com.example.demo.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRequestDTO {
    public interface existGroup{}
    public interface addGroup{}
    public interface passwordGroup{}
    public interface updateGroup{}
    public interface deleteGroup{}

    private Long id;

    @NotBlank(groups = {existGroup.class, addGroup.class,updateGroup.class,deleteGroup.class})@Size(min=4)
    private String username;

    @NotBlank(groups = {addGroup.class,passwordGroup.class})@Size(min=4)
    private String password;

    @NotBlank(groups = {addGroup.class,updateGroup.class})
    private String nickname;

    @NotBlank(groups = {addGroup.class,updateGroup.class})
    private String email;
}
