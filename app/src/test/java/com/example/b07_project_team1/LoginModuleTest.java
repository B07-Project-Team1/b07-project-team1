package com.example.b07_project_team1;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static java.util.UUID.randomUUID;

import com.example.b07_project_team1.model.CustomerLoginModel;
import com.example.b07_project_team1.model.VendorLoginModel;
import com.example.b07_project_team1.presenter.CustomerLoginPresenter;
import com.example.b07_project_team1.presenter.VendorLoginPresenter;
import com.example.b07_project_team1.view.CustomerLoginView;
import com.example.b07_project_team1.view.VendorLoginView;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(MockitoJUnitRunner.class)
public class LoginModuleTest {
    @Mock
    VendorLoginModel vendorModel;
    @Mock
    VendorLoginView vendorView;
    @Mock
    CustomerLoginModel customerModel;
    @Mock
    CustomerLoginView customerView;

    /*
    Don't allow user (vendor or customer) to input empty fields
     */
    @Test
    public void testEmptyFields() {
        VendorLoginPresenter vp = new VendorLoginPresenter(vendorView, vendorModel);
        CustomerLoginPresenter cp = new CustomerLoginPresenter(customerView, customerModel);

        // vendor empty fields
        vp.signIn("", randomUUID().toString());
        vp.signIn(randomUUID().toString(), "");
        verify(vendorView, times(2)).displayMessage("You have empty fields.");

        // customer empty fields
        cp.signIn("", randomUUID().toString());
        cp.signIn(randomUUID().toString(), "");
        verify(customerView, times(2)).displayMessage("You have empty fields.");
    }

    /*
    Make sure model sign-in is call when fields are non-empty
     */
    @Test
    public void testNonEmptyFields() {
        VendorLoginPresenter vp = new VendorLoginPresenter(vendorView, vendorModel);
        CustomerLoginPresenter cp = new CustomerLoginPresenter(customerView, customerModel);

        // vendor
        String vendorEmail = randomUUID().toString();
        String vendorPassword = randomUUID().toString();
        vp.signIn(vendorEmail, vendorPassword);
        verify(vendorModel).signIn(vp, vendorEmail, vendorPassword);

        // customer
        String customerEmail = randomUUID().toString();
        String customerPassword = randomUUID().toString();
        vp.signIn(customerEmail, customerPassword);
        verify(vendorModel).signIn(vp, customerEmail, customerPassword);
    }

    /*
    For any (non-special case) database authentication error, ensure the view displays the error
     */
    @Test
    public void testGeneralLoginError() {
        VendorLoginPresenter vp = new VendorLoginPresenter(vendorView, vendorModel);
        CustomerLoginPresenter cp = new CustomerLoginPresenter(customerView, customerModel);

        String vendorEmail = randomUUID().toString();
        String customerEmail = randomUUID().toString();
        String vendorPass = randomUUID().toString();
        String customerPass = randomUUID().toString();

        String sampleDBError = randomUUID().toString();

        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) {
                vp.setMessage(sampleDBError);
                return null;
            }
        }).when(vendorModel).signIn(vp, vendorEmail, vendorPass);

        vp.signIn(vendorEmail, vendorPass);
        verify(vendorView).displayMessage(sampleDBError);

        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) {
                cp.setMessage(sampleDBError);
                return null;
            }
        }).when(customerModel).signIn(cp, customerEmail, customerPass);

        cp.signIn(customerEmail, customerPass);
        verify(vendorView).displayMessage(sampleDBError);
    }

    /*
     Ensuring that custom "invalid password" message is functioning properly
     */
    @Test
    public void testInvalidPassword() {
        VendorLoginPresenter vp = new VendorLoginPresenter(vendorView, vendorModel);
        CustomerLoginPresenter cp = new CustomerLoginPresenter(customerView, customerModel);

        String vendorEmail = randomUUID().toString();
        String customerEmail = randomUUID().toString();
        String vendorPass = randomUUID().toString();
        String customerPass = randomUUID().toString();

        String sampleDBError = "The password is invalid or the user does not have a password.";

        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) {
                vp.setMessage(sampleDBError);
                return null;
            }
        }).when(vendorModel).signIn(vp, vendorEmail, vendorPass);

        vp.signIn(vendorEmail, vendorPass);
        verify(vendorView).displayMessage("Incorrect password.");

        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) {
                cp.setMessage(sampleDBError);
                return null;
            }
        }).when(customerModel).signIn(cp, customerEmail, customerPass);

        cp.signIn(customerEmail, customerPass);
        verify(vendorView).displayMessage("Incorrect password.");
    }


    /*
     Ensuring that custom "no account" message is functioning properly
     */
    @Test
    public void testNoAccount() {
        VendorLoginPresenter vp = new VendorLoginPresenter(vendorView, vendorModel);
        CustomerLoginPresenter cp = new CustomerLoginPresenter(customerView, customerModel);

        String vendorEmail = randomUUID().toString();
        String customerEmail = randomUUID().toString();
        String vendorPass = randomUUID().toString();
        String customerPass = randomUUID().toString();

        String sampleDBError = "There is no user record corresponding to this identifier. The user may have been deleted.";

        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) {
                vp.setMessage(sampleDBError);
                return null;
            }
        }).when(vendorModel).signIn(vp, vendorEmail, vendorPass);

        vp.signIn(vendorEmail, vendorPass);
        verify(vendorView).displayMessage("No account associated with email.");

        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) {
                cp.setMessage(sampleDBError);
                return null;
            }
        }).when(customerModel).signIn(cp, customerEmail, customerPass);

        cp.signIn(customerEmail, customerPass);
        verify(vendorView).displayMessage("No account associated with email.");
    }

    /*
    Ensuring the customer is directed to the mall activity on successful login
     */
    @Test
    public void testCustomerSuccessfulLogin() {
        CustomerLoginPresenter cp = new CustomerLoginPresenter(customerView, customerModel);
        String customerEmail = randomUUID().toString();
        String customerPass = randomUUID().toString();
        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) {
                cp.setupNewActivity();
                return null;
            }
        }).when(customerModel).signIn(cp, customerEmail, customerPass);
        cp.signIn(customerEmail, customerPass);
        verify(customerView).launchAnimationView();
    }

    /*
    Ensure the vendor is taken to the vendor setup page if their login was successful but they have not set up their account
     */
    @Test
    public void testVendorSuccessfulLoginNotSetup() {
        VendorLoginPresenter vp = new VendorLoginPresenter(vendorView, vendorModel);
        String vendorEmail = randomUUID().toString();
        String vendorPass = randomUUID().toString();
        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) {
                vp.setupNewActivity(null, null);
                return null;
            }
        }).when(vendorModel).signIn(vp, vendorEmail, vendorPass);
        vp.signIn(vendorEmail, vendorPass);
        verify(vendorView).launchPageTransition(true, null, null);
    }

    /*
    Ensure the vendor is taken to the store  page if their login was successful and they have set up their account
     */
    @Test
    public void testVendorSuccessfulLoginIsSetup() {
        VendorLoginPresenter vp = new VendorLoginPresenter(vendorView, vendorModel);
        String vendorEmail = randomUUID().toString();
        String vendorPass = randomUUID().toString();
        String brandName = randomUUID().toString();
        String logoUrl = randomUUID().toString();

        when(vendorModel.getUID()).thenReturn("UID");

        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) {
                vp.setupNewActivity(brandName, logoUrl);
                return null;
            }
        }).when(vendorModel).signIn(vp, vendorEmail, vendorPass);
        vp.signIn(vendorEmail, vendorPass);
        verify(vendorView).launchPageTransition(false, logoUrl, "UID");
    }

    /*
    Ensure user data is generated when the user is successfully signed in
     */
    @Test
    public void testUserDataGeneration() {
        VendorLoginPresenter vp = new VendorLoginPresenter(vendorView, vendorModel);
        String sampleUID = randomUUID().toString();
        when(vendorModel.getUID()).thenReturn(sampleUID);
        vp.onSignIn();
        verify(vendorModel).generateUserData(vp, sampleUID);
    }



}