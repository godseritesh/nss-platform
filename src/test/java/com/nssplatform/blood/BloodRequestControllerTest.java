package com.nssplatform.blood;

import com.nssplatform.blood.BloodRequest;
import com.nssplatform.blood.BloodRequestController;
import com.nssplatform.blood.BloodRequestService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BindingResult;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BloodRequestControllerTest {

    @Mock
    private BloodRequestService bloodRequestService;

    @Mock
    private Validator validator;

    @InjectMocks
    private BloodRequestController bloodRequestController;

    @Test
    public void testCreateBloodRequest_ServiceError() {
        BloodRequest bloodRequest = new BloodRequest();
        BindingResult bindingResult = new BindingResult(bloodRequest, "bloodRequest");
        
        doThrow(new RuntimeException("Error creating blood request")).when(bloodRequestService).createBloodRequest(bloodRequest);
        
        RuntimeException exception = assertThrows(RuntimeException.class, () -> bloodRequestController.createBloodRequest(bloodRequest, bindingResult));
        
        // Additional assertions can be added to test specific exception properties
    }

    @Test
    public void testCreateBloodRequest_ValidatorError() {
        BloodRequest bloodRequest = new BloodRequest();
        BindingResult bindingResult = new BindingResult(bloodRequest, "bloodRequest");
        
        when(validator.validate(bloodRequest)).thenReturn(Set.of(new ConstraintViolation<String, Object>("Error", "test", null, null)));
        
        ConstraintViolationException exception = assertThrows(ConstraintViolationException.class, () -> bloodRequestController.createBloodRequest(bloodRequest, bindingResult));
        
        // Additional assertions can be added to test specific constraint violations
    }

    @Test
    public void testCreateBloodRequest_ValidInput() {
        BloodRequest bloodRequest = new BloodRequest();
        // Initialize bloodRequest with valid data
        BindingResult bindingResult = new BindingResult(bloodRequest, "bloodRequest");
        
        bloodRequestController.createBloodRequest(bloodRequest, bindingResult);
        
        // Additional assertions can be added to test the successful creation of blood request
    }
}