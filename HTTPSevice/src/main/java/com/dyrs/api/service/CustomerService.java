package com.dyrs.api.service;

import com.dyrs.api.entity.CustomerGuest;
import com.dyrs.api.entity.IMUser;

import java.util.List;

public interface CustomerService {
    IMUser getCustomer(String jiajuid);
    void saveAndReplaceCustomerGesut(CustomerGuest customerGuest);
    List<String> getOnlineGuestIDsByCustomerID(String customer_id);
    List<String> getOnlineCustomerIDsByGuestID(String customer_id);
    void deleteCustomerGuestByCustomerID(String customerID);
    void deleteCustomerGuestByGuestID(String guestID);
}
