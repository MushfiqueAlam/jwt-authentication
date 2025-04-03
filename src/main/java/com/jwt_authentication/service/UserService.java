package com.jwt_authentication.service;


import com.jwt_authentication.model.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class UserService {

        private List<User> store=new ArrayList<>();

        public UserService(){
                store.add(new User(UUID.randomUUID().toString(),"Mushfique","mushfique@gmail.com"));
                store.add(new User(UUID.randomUUID().toString(),"Athar","athar@gmail.com"));
                store.add(new User(UUID.randomUUID().toString(),"Rahman","rahman@gmail.com"));
                store.add(new User(UUID.randomUUID().toString(),"Arif","arif@gmail.com"));
        }

        public List<User> getUsers(){
            return this.store;
        }
}
