package com.almabranding.yummi.Apis;

import com.almabranding.yummi.models.BankModel;
import com.almabranding.yummi.models.CardModel;
import com.almabranding.yummi.models.ConflictResolutionAnswerModel;
import com.almabranding.yummi.models.ConflictResolutionBaseClass;
import com.almabranding.yummi.models.ConflictResolutionReturnClass;
import com.almabranding.yummi.models.CreateEventmodel;
import com.almabranding.yummi.models.EventListModel;
import com.almabranding.yummi.models.EventModel;
import com.almabranding.yummi.models.LocationModel;
import com.almabranding.yummi.models.LoginModel;
import com.almabranding.yummi.models.LoginResponseModel;
import com.almabranding.yummi.models.Logout_model;
import com.almabranding.yummi.models.MediaModel;
import com.almabranding.yummi.models.NotificationModel;
import com.almabranding.yummi.models.PaymentModel;
import com.almabranding.yummi.models.PaymentTokenModel;
import com.almabranding.yummi.models.PerformerListModel;
import com.almabranding.yummi.models.PerformerLoginResponseModel;
import com.almabranding.yummi.models.PerformerModel;
import com.almabranding.yummi.models.PerformerUpdateModel;
import com.almabranding.yummi.models.RegFifthModel;
import com.almabranding.yummi.models.RegFourthModel;
import com.almabranding.yummi.models.RegSecondModel;
import com.almabranding.yummi.models.RegThirdModel;
import com.almabranding.yummi.models.RegistrationEightModel;
import com.almabranding.yummi.models.RegistrationEightPriceModel;
import com.almabranding.yummi.models.RegistrationEightResponseModel;
import com.almabranding.yummi.models.RegistrationFirstResponseModel;
import com.almabranding.yummi.models.RegistrationFirstResponsePerformerModel;
import com.almabranding.yummi.models.RegistrationSecondResponseModel;
import com.almabranding.yummi.models.RegistrationSecondResponsePerformerModel;
import com.almabranding.yummi.models.RegistrationThirdResponseModel;
import com.almabranding.yummi.models.RegistrationThirdResponsePerformerModel;
import com.almabranding.yummi.models.ResetPassModel;
import com.almabranding.yummi.models.SecurityContactModel;
import com.almabranding.yummi.models.UpdateMailModel;
import com.almabranding.yummi.models.UpdatePassModel;
import com.almabranding.yummi.models.chat.ChatMessageModel;
import com.almabranding.yummi.models.chat.ChatModel;
import com.almabranding.yummi.models.location.LocationPushModel;
import com.almabranding.yummi.models.third.GenderModel;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by ioshero on 11/04/16.
 */
public interface NetworkCallApiInterface {

    @POST("clients/login")
    Call<LoginResponseModel> createUser(@Body LoginModel user);

    @POST("clients/logout")
    Call<ResponseBody> logout(@Query("access_token") String token, @Body Logout_model user);

    @POST("clients/signup_email")
    Call<RegistrationFirstResponseModel> signUpClient(@Body LoginModel user);

    @POST("clients/{id}/signup_name")
    Call<RegistrationSecondResponseModel> signUpClientSecond(@Path("id") String userdId, @Body RegSecondModel nameModel, @Query("access_token") String token);

    @POST("clients/{id}/signup_gender")
    Call<RegistrationThirdResponseModel> signUpClientThird(@Path("id") String userdId, @Body RegThirdModel genderModel, @Query("access_token") String token);

    @POST("events")
    Call<EventModel> createEvent(@Body CreateEventmodel model, @Query("access_token") String token);

    @PUT("events")
    Call<EventModel> updateEvent(@Body EventModel model, @Query("access_token") String token);

    @GET("clients/{id}/eventList")
    Call<List<EventListModel>> getClientEvents(@Path("id") String userdId, @Query("access_token") String token);

    @POST("performers/login")
    Call<PerformerLoginResponseModel> perCreateUser(@Body LoginModel user);

    @POST("performers/logout")
    Call<ResponseBody> perLogout(@Query("access_token") String token, @Body Logout_model user);

    @POST("performers/signup_email")
    Call<RegistrationFirstResponsePerformerModel> perSignUpClient(@Body LoginModel user);

    @POST("performers/{id}/signup_name")
    Call<RegistrationSecondResponsePerformerModel> perSignUpClientSecond(@Path("id") String userdId, @Body RegSecondModel nameModel, @Query("access_token") String token);

    @POST("performers/{id}/signup_gender")
    Call<RegistrationThirdResponsePerformerModel> perSignUpClientThird(@Path("id") String userdId, @Body RegThirdModel genderModel, @Query("access_token") String token);

    @POST("performers/{id}/signup_age")
    Call<RegistrationThirdResponsePerformerModel> perSignUpClientFourth(@Path("id") String userdId, @Body RegFourthModel dataModel, @Query("access_token") String token);

    @POST("performers/{id}/signup_body_details")
    Call<RegistrationThirdResponsePerformerModel> perSignUpClientFifth(@Path("id") String userdId, @Body RegFifthModel dataModel, @Query("access_token") String token);

    @Multipart
    @POST("performers/{id}/signup_images")
    Call<ResponseBody> upload(@Path("id") String userdId,
                              @Part MultipartBody.Part order,
                              @Part MultipartBody.Part file, @Query("access_token") String token);

    @Multipart
    @POST("performers/{id}/signup_idImages")
    Call<ResponseBody> uploadidImages(@Path("id") String userdId,
                                      @Part MultipartBody.Part order,
                                      @Part MultipartBody.Part file, @Query("access_token") String token);


    @GET("appUsers/myProfile")
    Call<PerformerLoginResponseModel> myProfile(@Query("access_token") String token);

    @POST("performers/{id}/signup_services")
    Call<ResponseBody> registrationServices(@Path("id") String userdId, @Body RegistrationEightModel dataModel, @Query("access_token") String token);


    @POST("performers/{id}/setPrices")
    Call<ResponseBody> registrationPrices(@Path("id") String userdId, @Body RegistrationEightPriceModel dataModel, @Query("access_token") String token);

    @POST("serviceTypes/services")
    Call<List<RegistrationEightResponseModel>> getServiceList(@Query("access_token") String token);

    @GET("genders")
    Call<List<GenderModel>> getGenderList(@Query("access_token") String token);

    @GET("hairColors")
    Call<List<GenderModel>> getHairColor(@Query("access_token") String token);

    @GET("bustSizes")
    Call<List<GenderModel>> getBustSize(@Query("access_token") String token);

    @GET("bodyTypes")
    Call<List<GenderModel>> getBodyType(@Query("access_token") String token);

    @GET("performers")
    Call<List<PerformerListModel>> getPreformers(@Query("access_token") String token);

    @GET("performers")
    Call<List<PerformerListModel>> getPreformerswithFilter(@Query("access_token") String token, @Query("filter") String filter);

    @GET("performers/{id}")
    Call<PerformerModel> getPerformer(@Path("id") String performerId, @Query("access_token") String token);

    @POST("performers/nearer")
    Call<List<PerformerListModel>> getPerformersNearer(@Query("access_token") String token, @Body LocationPushModel locationModel);

    @POST("performers/nearer")
    Call<List<PerformerListModel>> getPerformersNearerwithFilter(@Query("access_token") String token, @Query("filter") String filter, @Body LocationPushModel locationModel);

    @POST("clients/{id}/cards")
    Call<CardModel> postCards(@Path("id") String userdId, @Query("access_token") String token, @Body PaymentTokenModel dataModel);

    @GET("events/{id}")
    Call<EventModel> getEvent(@Path("id") String eventId, @Query("access_token") String token);

    @DELETE("events/{id}")
    Call<ResponseBody> deleteEvent(@Path("id") String userdId, @Query("access_token") String token);

    @DELETE("clients/{id}/chats/{fk}/hide")//delete /clients/{id}/chats/{fk}/hide
    Call<ResponseBody> deleteChat(@Path("id") String userdId,@Path("fk") String chstID, @Query("access_token") String token);


    @DELETE("performers/{id}/chats/{fk}/hide")//delete /clients/{id}/chats/{fk}/hide
    Call<ResponseBody> deleteChatPer(@Path("id") String userdId,@Path("fk") String chstID, @Query("access_token") String token);


    @POST("clients/{id}/charges")
    Call<ResponseBody> addChargeToken(@Path("id") String userdId, @Query("access_token") String token, @Body PaymentModel dataModel);

    @PUT("clients/{id}/favourites/rel/{fk}")
    Call<ResponseBody> putFavourite(@Path("id") String userdId, @Path("fk") String performerId, @Query("access_token") String token);

    @DELETE("clients/{id}/favourites/rel/{fk}")
    Call<ResponseBody> deleteFavourite(@Path("id") String userdId, @Path("fk") String performerId, @Query("access_token") String token);

    @GET("clients/{id}/favourites")
    Call<List<PerformerListModel>> getFavouritesList(@Path("id") String userdId, @Query("access_token") String token);

    @GET("clients/{id}/notifications")
    Call<List<NotificationModel>> getNotifications(@Path("id") String userdId, @Query("access_token") String token);

    @GET("performers/{id}/notifications")
    Call<List<NotificationModel>> getNotificationsPer(@Path("id") String userdId, @Query("access_token") String token);


    @GET("clients/{id}/chats")
    Call<List<ChatModel>> getChats(@Path("id") String userdId, @Query("access_token") String token);

    @GET("chats/{id}/chatMessages")
    Call<List<ChatMessageModel>> getChatMessages(@Path("id") String userdId, @Query("access_token") String token);


    @GET("performers/{id}/chats")
    Call<List<ChatModel>> getChatsPer(@Path("id") String userdId, @Query("access_token") String token);

    @GET("performers/{id}/eventList")
    Call<List<EventListModel>> getClientEventsPer(@Path("id") String userdId, @Query("access_token") String token);


    @POST("performers/{id}/bankAccount")
    Call<BankModel> postBankAccount(@Path("id") String userdId, @Body BankModel dataModel, @Query("access_token") String token);

    @PUT("performers/{id}/bankAccount")
    Call<BankModel> updateBankAccount(@Path("id") String userdId, @Query("access_token") String token, @Body BankModel dataModel);

    @GET("performers/{id}/bankAccount")
    Call<BankModel> getBankAccount(@Path("id") String userdId, @Query("access_token") String token);

    @GET("clients/{id}/mediaGallery")
    Call<List<MediaModel>> mediaGallery(@Path("id") String userdId, @Query("access_token") String token);


    @PUT("performers/{id}")
    Call<ResponseBody> updatePerformer(@Path("id") String userdId, @Query("access_token") String token, @Body PerformerUpdateModel user);

    @PUT("performers/{id}/changePassword")
    Call<ResponseBody> updatePasswordPassword(@Path("id") String userdId, @Query("access_token") String token, @Body UpdatePassModel model);

    @POST("clients/{id}/securityContact")
    Call<ResponseBody> postSecurityContact(@Path("id") String userdId, @Body SecurityContactModel dataModel, @Query("access_token") String token);

    @PUT("performers/{id}/interestedIn/rel/{fk}")
    Call<ResponseBody> updateInterestedInPerformer(@Path("id") String userdId, @Path("fk") String genderId, @Query("access_token") String token);

    @PUT("clients/{id}/interestedIn/rel/{fk}")
    Call<ResponseBody> updateInterestedIn(@Path("id") String userdId, @Path("fk") String genderId, @Query("access_token") String token);



    @PUT("performers/{id}/updateEmail")
    Call<ResponseBody> updatePerEmail(@Path("id") String userdId, @Query("access_token") String token, @Body UpdateMailModel model);

    @PUT("clients/{id}/updateEmail")
    Call<ResponseBody> updateEmail(@Path("id") String userdId, @Query("access_token") String token, @Body UpdateMailModel model);

    @PUT("clients/{id}/changePassword")
    Call<ResponseBody> updatePasswordClient(@Path("id") String userdId, @Query("access_token") String token, @Body UpdatePassModel model);

    @POST("clients/reset ")
    Call<ResponseBody> clientReset(@Body ResetPassModel email);


    @POST("performers/reset")
    Call<ResponseBody> performerReset(@Body ResetPassModel email);

    @POST("serviceTypes/servicesByGender/{genderId}")
    Call<List<RegistrationEightResponseModel>> getServiceListByGender(@Path("genderId") String genderId, @Query("access_token") String token);

    @POST("performers/{id}/save_location")
    Call<ResponseBody> postLocationPer(@Path("id") String userdId, @Body LocationPushModel locationModel, @Query("access_token") String token);

//    @POST("clients/save_location")
//    Call<ResponseBody> postLocation(@Body LocationPushModel locationModel, @Query("access_token") String token);

    @Multipart
    @POST("performers/{pid}/imageRequests/{rid}/reply")
    Call<ResponseBody> uploadPicture(@Path("pid") String performerId, @Path("rid") String requestId, @Query("access_token") String token, @Part MultipartBody.Part file);


    @GET("clients/CrQuestions")
    Call<List<ConflictResolutionBaseClass>> getCrQuestions(@Query("access_token") String token);

    @GET("performers/CrQuestions")
    Call<List<ConflictResolutionBaseClass>> getCrQuestionsPrformer(@Query("access_token") String token);

    @POST("performers/{id}/conflictResolutions")
    Call<ResponseBody> postPerformerCR(@Path("id") String userdId, @Query("access_token") String token, @Body ConflictResolutionReturnClass model);

    @POST("clients/{id}/conflictResolutions")
    Call<ResponseBody> postClientCR(@Path("id") String userdId, @Query("access_token") String token, @Body ConflictResolutionReturnClass model);


}