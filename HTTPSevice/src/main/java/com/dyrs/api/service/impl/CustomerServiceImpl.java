package com.dyrs.api.service.impl;

import com.dyrs.api.dao.CustomerGuestDAO;
import com.dyrs.api.dao.UserDAO;
import com.dyrs.api.entity.CustomerGuest;
import com.dyrs.api.entity.IMUser;
import com.dyrs.api.service.CustomerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

@Service
public class CustomerServiceImpl implements CustomerService {
    private static final Logger LOG = LoggerFactory.getLogger(CustomerServiceImpl.class);
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private UserDAO userDAO;
    @Autowired
    private CustomerGuestDAO customerGuestDAO;

    @Override
    public IMUser getCustomer(String jiajuid) {
        Set<String> keys = redisTemplate.keys("CustomerServicerSessions:*");
        if (keys != null && !keys.isEmpty()) {
            CustomerGuest customerGuest = customerGuestDAO.findFirstByGuestID(jiajuid);
            //  先查询他有没有关联的客服，如果有就取出原来的客服
            if (customerGuest != null && keys.contains("CustomerServicerSessions:" + customerGuest.getCustomerID())){
                return userDAO.getUserInfo(customerGuest.getCustomerID());
            } else {
                //  如果没有对应客服，就随机分配
                String[] keys_array = keys.toArray(new String[]{});
                Random random = new Random();
                int index = random.nextInt(keys_array.length);
                BoundSetOperations<String, String> operations = redisTemplate.boundSetOps(keys_array[index]);
                String username = operations.randomMember();
                saveAndReplaceCustomerGesut(new CustomerGuest(username, jiajuid));
                return userDAO.getUserInfo(username);
            }
        }
        return null;
    }

    @Override
    public void saveAndReplaceCustomerGesut(CustomerGuest customerGuest) {
//        Example<CustomerGuest> example = Example.of(customerGuest, ExampleMatcher.matching().withMatcher("customer_id", ExampleMatcher.GenericPropertyMatchers.exact()).withMatcher("geust_id", ExampleMatcher.GenericPropertyMatchers.exact()).withIgnorePaths("id"));
        if (exit(customerGuest)) {
            delete(customerGuest);
        }
        customerGuestDAO.save(customerGuest);
    }

    @Override
    public List<String> getOnlineGuestIDsByCustomerID(String customer_id) {
        List<CustomerGuest> cgs = customerGuestDAO.findAllByCustomerID(customer_id);
        List<String> results = new ArrayList<>();
        if (cgs != null && !cgs.isEmpty()) {
            for (CustomerGuest customerGuest : cgs) {
                results.add(customerGuest.getGuestID());
            }
        }

        return results;
    }

    @Override
    public List<String> getOnlineCustomerIDsByGuestID(String guest_id) {
        List<CustomerGuest> cgs = customerGuestDAO.findAllByGuestID(guest_id);
        List<String> results = new ArrayList<>();
        if (cgs != null && !cgs.isEmpty()) {
            for (CustomerGuest customerGuest : cgs) {
                results.add(customerGuest.getCustomerID());
            }
        }
        return results;
    }

    @Override
    public void deleteCustomerGuestByCustomerID(String customerID) {
        LOG.debug("delete CustomerGuest by customer ID --> " + customerID);
        customerGuestDAO.deleteAllByCustomerID(customerID);
    }

    @Override
    public void deleteCustomerGuestByGuestID(String guestID) {
        LOG.debug("delete CustomerGuest by guest ID --> " + guestID);
        customerGuestDAO.deleteAllByGuestID(guestID);
    }


    private void delete(CustomerGuest customerGuest) {
        customerGuestDAO.deleteByCustomerIDAndGuestID(customerGuest.getCustomerID(), customerGuest.getGuestID());
    }

    private boolean exit(CustomerGuest customerGuest) {
        CustomerGuest customerGuest1 = customerGuestDAO.findFirstByCustomerIDAndGuestID(customerGuest.getCustomerID(), customerGuest.getGuestID());
        return customerGuest1 != null;
    }
}
