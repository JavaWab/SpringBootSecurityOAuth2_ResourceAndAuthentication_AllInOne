package com.dyrs.api.service;

import com.dyrs.api.entity.IMUser;

public interface IMUserService {
    IMUser getGuestIMUser(String other_id);

    IMUser getServicer(String other_id);

}
