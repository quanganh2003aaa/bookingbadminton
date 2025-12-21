package com.example.bookingbadminton.model.dto;

import com.example.bookingbadminton.model.entity.Account;
import com.example.bookingbadminton.model.entity.Owner;
import com.example.bookingbadminton.model.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountDTO {
    private String id;
    private String name;
    private String gmail;
    private String msisdn;
    private String token;
    private String avatar;

    public AccountDTO(Account account, User user){
        this.id = user.getId().toString();
        this.gmail = account.getGmail();
        this.msisdn = account.getMsisdn();
        this.name = user.getName();
        this.avatar = user.getAvatar();
    }

    public AccountDTO(Account account, Owner owner){
        this.id = owner.getId().toString();
        this.gmail = account.getGmail();
        this.msisdn = account.getMsisdn();
        this.name = owner.getName();
        this.avatar = owner.getAvatar();
    }
}
