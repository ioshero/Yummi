package com.almabranding.yummi.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ListFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.almabranding.yummi.Apis.NetworkCallApiInterface;
import com.almabranding.yummi.MainActivity;
import com.almabranding.yummi.R;
import com.almabranding.yummi.WelcomeActivity;
import com.almabranding.yummi.adapter.NotificationsAdapter;
import com.almabranding.yummi.models.EventModel;
import com.almabranding.yummi.models.NotificationModel;
import com.almabranding.yummi.utils.PictureCroop;
import com.almabranding.yummi.utils.RealPathUtil;
import com.almabranding.yummi.utils.YummiUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by ioshero on 03/05/16.
 * <p/>
 * case event = "event"
 * case imageRequest = "imageRequest"
 * case tip = "tip"
 * case chat = "chat"
 * case undefined = "undefined"
 * }
 * <p/>
 * public enum NotificationType: String {
 * case undifined = "undefined"
 * case eventInvitation = "eventInvitation"
 * case eventIsReadyToBePaid = "eventIsReadyToBePaid"
 * case performerAdded = "performerAdded"
 * case cancelEventInvitation = "cancelEventInvitation"
 * case performerDeleted = "performerDeleted"
 * case performerAccepted = "performerAccepted"
 * case performerRejected = "performerRejected"
 * case performerCheckedIn = "performerCheckedIn"
 * case guestAdded = "guestAdded"
 * case guestDeleted = "guestDeleted"
 * case guestAccepted = "guestAccepted"
 * case guestRejected = "guestRejected"
 * case guestCheckedIn = "guestCheckedIn"
 * case serviceAdded = "serviceAdded"
 * case serviceDeleted = "serviceDeleted"
 * case eventStarted = "eventStarted"
 * case eventCancelled = "eventCancelled"
 * case tipAdded = "tipAdded"
 * case imageRequest = "newImageRequest"
 * case imageRequestResponse = "imageRequestReplayed"
 * case newChat = "newChat"
 * case newPerformerMessage = "newPerformerMessage"
 * case newClientMessage = "newClientMessage"
 * <p/>
 * func getDescription() -> String {
 * switch self {
 * case .eventInvitation:
 * return "You have been invited to event \"%@\""
 * case .eventIsReadyToBePaid:
 * return "\"%@\" event is ready to be paid"
 * case .performerAdded:
 * return "New performer added to event \"%@\""
 * case .cancelEventInvitation:
 * return "\"%@\" event invitation has been canceled"
 * case .performerDeleted:
 * return "Performer has been removed from event \"%@\""
 * case .performerAccepted:
 * return "Performer accept invitation for event \"%@\""
 * case .performerRejected:
 * return "Performer reject invitation for event \"%@\""
 * case .performerCheckedIn:
 * return "Performer is in event"
 * case .guestAdded:
 * return "You have been invited to event \"%@\""
 * case .guestAccepted:
 * return "Guest accept invitation for event \"%@\""
 * case .guestDeleted:
 * return "Guest has been removed from event \"%@\""
 * case .eventStarted:
 * return "\"%@\" event has been started"
 * case .eventCancelled:
 * return "\"%@\" event has been canceled"
 * case .tipAdded:
 * return "\"%@\" new Tip"
 * case .serviceAdded:
 * return "\"%@\" new services added"
 * case .serviceDeleted:
 * return "Service was removed from event \"%@\""
 * case .guestCheckedIn:
 * return "Guest is in place"
 * case .guestRejected:
 * return "Guest reject invitation for event \"%@\""
 * case .undifined:
 * return "undefined notifiaction type"
 * case .imageRequest:
 * return "You recived image request from \"%@\""
 * case .imageRequestResponse:
 * return "You recived new image"
 * default:
 * return ""
 * }
 * }
 * }
 */


public class NotificationsFragment extends ListFragment {


    private String requestId = "";

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    private String clientName = "";

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //setHasOptionsMenu(true);
        final View view = inflater.inflate(R.layout.fragment_notificationss, container, false);
        if (getActivity().findViewById(R.id.send_image_close) != null)
            getActivity().findViewById(R.id.send_image_close).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((AppCompatActivity) getActivity()).getSupportActionBar().show();
                    getActivity().findViewById(R.id.send_picture_layout).setVisibility(View.GONE);
                    ((ImageButton) getActivity().findViewById(R.id.send_image_select)).setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                    ((ImageButton) getActivity().findViewById(R.id.send_image_select)).setImageResource(R.mipmap.photo_icon);
                    getActivity().findViewById(R.id.send_image_button).setOnClickListener(null);
                    requestId = "";
                    clientName = "";
                }
            });


        if (getActivity().findViewById(R.id.send_image_close) != null)
            if (!requestId.isEmpty()) {
                ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
                getActivity().findViewById(R.id.send_picture_layout).setVisibility(View.VISIBLE);
                ((TextView) getActivity().findViewById(R.id.send_image_texview)).setText("You received image request from " + clientName);
                getActivity().findViewById(R.id.send_image_button).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        uploadImage(MainActivity.userId, requestId);

                    }
                });
            }

        if (getActivity().findViewById(R.id.send_image_close) != null)
            getActivity().findViewById(R.id.send_image_select).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dispatchTakePictureIntent();
                }
            });
//        setEmptyText("No Notifications");

        return view;
    }

    static final int REQUEST_IMAGE_CAPTURE = 1;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        selectedImagePath_one = "file:" + image.getAbsolutePath();
        return image;
    }


    private void setPic() {
        Bitmap bitmap = BitmapFactory.decodeFile(selectedImagePath_one.substring(5, selectedImagePath_one.length()));
        ((ImageButton) getActivity().findViewById(R.id.send_image_select)).setScaleType(ImageView.ScaleType.CENTER_CROP);
        ((ImageButton) getActivity().findViewById(R.id.send_image_select)).setImageBitmap(bitmap);


    }


    private String selectedImagePath_one = "";

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == getActivity().RESULT_OK) {
            setPic();
        }
    }


    private void uploadImage(String performerID, String requestID) {
        ((MainActivity) getActivity()).addOverlay();
        try {


            Bitmap yourSelectedImage = YummiUtils.scaleBitmap(BitmapFactory.decodeFile(selectedImagePath_one.substring(5, selectedImagePath_one.length())));
            File file = new File(selectedImagePath_one.substring(5, selectedImagePath_one.length()));//or selectedImagePath_one
//PictureCroop.CROPPED_IMAGE_FILE_URI1.getPath()

            FileOutputStream fOut = new FileOutputStream(file);
            yourSelectedImage.compress(Bitmap.CompressFormat.JPEG, 70, fOut);
            fOut.flush();
            fOut.close();


            NetworkCallApiInterface service = ((MainActivity) getActivity()).retrofit.create(NetworkCallApiInterface.class);


            RequestBody requestFile =
                    RequestBody.create(MediaType.parse("multipart/form-data"), file);

            MultipartBody.Part body =
                    MultipartBody.Part.createFormData("file", String.valueOf((new Date()).getTime()) + "_" + file.getName(), requestFile);

            Call<ResponseBody> call = service.uploadPicture(performerID, requestID, MainActivity.token, body);

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    ((MainActivity) getActivity()).removeView();
                    if (response.body() == null) {

                        MainActivity.showAllert("Error", "Failed to upload the picture");
                    }
                    if (getActivity().findViewById(R.id.send_image_close) != null)
                        if (getView() != null) {
                            ((AppCompatActivity) getActivity()).getSupportActionBar().show();
                            getActivity().findViewById(R.id.send_picture_layout).setVisibility(View.GONE);
                            ((ImageButton) getActivity().findViewById(R.id.send_image_select)).setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                            ((ImageButton) getActivity().findViewById(R.id.send_image_select)).setImageResource(R.mipmap.photo_icon);
                            getActivity().findViewById(R.id.send_image_button).setOnClickListener(null);
                        }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    t.toString();

                    MainActivity.showAllert("Network Error", "Failed to upload the picture");
                    ((MainActivity) getActivity()).removeView();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void setUpActionBar() {
        if (YummiUtils.isPreformer(getContext()))
            ((MainActivity) getActivity()).setLadies();
        else
            ((MainActivity) getActivity()).setNone();

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.getCustomView().findViewById(R.id.actionbar_titleimageview).setVisibility(View.INVISIBLE);
        TextView title = (TextView) actionBar.getCustomView().findViewById(R.id.actionbar_titleview);
        title.setVisibility(View.VISIBLE);
        title.setText("Notifications");


        actionBar.getCustomView().findViewById(R.id.drawer_textview_done).setVisibility(View.INVISIBLE);
        actionBar.getCustomView().findViewById(R.id.drawer_imageview_done).setVisibility(View.INVISIBLE);

        actionBar.getCustomView().findViewById(R.id.drawer_imageview).setVisibility(View.VISIBLE);
        actionBar.getCustomView().findViewById(R.id.drawer_textview).setVisibility(View.INVISIBLE);

        actionBar.getCustomView().findViewById(R.id.actionbar_titleimageview).setVisibility(View.VISIBLE);
        actionBar.getCustomView().findViewById(R.id.actionbar_titleview).setVisibility(View.INVISIBLE);


    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setUpActionBar();
        if (!result.isEmpty())
            setUpAdapter();

        getNotifications();
        if (getActivity() != null)
            if (getActivity().findViewById(R.id.notif_count) != null) {
                getActivity().findViewById(R.id.notif_count).setVisibility(View.GONE);
            }


    }

    NotificationsAdapter adapter;
    static final ArrayList<NotificationModel> result = new ArrayList<NotificationModel>();
    ArrayList<NotificationModel> events = new ArrayList<NotificationModel>();

    private ArrayList<NotificationModel> getNotifications() {

        NetworkCallApiInterface service = MainActivity.retrofit.create(NetworkCallApiInterface.class);

        Call<List<NotificationModel>> call;
        if (!YummiUtils.isPreformer(getContext()))
            call = service.getNotifications(MainActivity.userId, MainActivity.token);
        else
            call = service.getNotificationsPer(MainActivity.userId, MainActivity.token);

        call.enqueue(new Callback<List<NotificationModel>>() {
                         @Override
                         public void onResponse(Call<List<NotificationModel>> call, final Response<List<NotificationModel>> response) {
//                    bustModelList = response.body();

                             if (response.body() != null) {
                                 result.clear();
                                 for (NotificationModel e : response.body()) {
                                     result.add(e);
                                 }
                                 setUpAdapter();
                             }

                         }

                         @Override
                         public void onFailure(Call<List<NotificationModel>> call, Throwable t) {
//                    showAllert("Network Error", t.getMessage());
                         }
                     }

        );

        return result;
    }

    public void setUpAdapter() {
        if (NotificationsFragment.this != null) {
            if (NotificationsFragment.this.getContext() != null) {
                adapter = new NotificationsAdapter(NotificationsFragment.this.getContext(), result);
                setListAdapter(adapter);
//                                         getListView().setEmptyView(getView().findViewById(R.id.emptyElement));
                if (getView() != null)
                    getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                             @Override
                                                             public void onItemClick(AdapterView<?> adapterView, View view, final int position, long l) {

                                                                 try {
                                                                     ((MainActivity) getActivity()).notificationRead(result.get(position).getId());
                                                                 } catch (Exception e) {
                                                                     e.printStackTrace();
                                                                 }

                                                                 switch (result.get(position).getType()) {
                                                                     case "newImageRequest": {
                                                                         try {
                                                                             AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                                                                             if (getActivity().findViewById(R.id.send_image_close) != null)
                                                                                 alertDialogBuilder.setPositiveButton("Take a shot", new DialogInterface.OnClickListener() {
                                                                                     @Override
                                                                                     public void onClick(DialogInterface dialogInterface, int i) {

                                                                                         ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
                                                                                         getActivity().findViewById(R.id.send_picture_layout).setVisibility(View.VISIBLE);
                                                                                         ((TextView) getActivity().findViewById(R.id.send_image_texview)).setText("You received image request from " + result.get(position).getClientName());
                                                                                         getActivity().findViewById(R.id.send_image_button).setOnClickListener(new View.OnClickListener() {
                                                                                             @Override
                                                                                             public void onClick(View view) {

                                                                                                 uploadImage(MainActivity.userId, result.get(position).getRequestId());

                                                                                             }
                                                                                         });

                                                                                     }
                                                                                 });
                                                                             alertDialogBuilder.setNegativeButton("OK", null);
                                                                             alertDialogBuilder.setTitle("New Image Request");

                                                                             AlertDialog alertDialog = alertDialogBuilder.create();
                                                                             alertDialog.show();
                                                                         } catch (Exception e) {
                                                                             //exception if it wants to show an message when the screen is dissmissed
                                                                             e.printStackTrace();
                                                                         }
                                                                         break;
                                                                     }

                                                                     case "newChat": {
                                                                         try {
//                                                                                                      AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
//                                                                                                      if (getActivity().findViewById(R.id.send_image_close) != null)
//                                                                                                          alertDialogBuilder.setPositiveButton("Start chating", new DialogInterface.OnClickListener() {
//                                                                                                              @Override
//                                                                                                              public void onClick(DialogInterface dialogInterface, int i) {
//                                                                                                                  ((MainActivity)getActivity()).startChat(result.get(position).getChatId());
//                                                                                                              }
//                                                                                                          });
//                                                                                                      alertDialogBuilder.setTitle("New chat from "+result.get(position).getClientName());
//
//                                                                                                      AlertDialog alertDialog = alertDialogBuilder.create();
//                                                                                                      alertDialog.show();

                                                                             ChatListFragment fragment = new ChatListFragment();
                                                                             fragment.setChatId(result.get(position).getChatId());
                                                                             android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                                                             fragmentManager.beginTransaction()
                                                                                     .addToBackStack(null)
                                                                                     .replace(R.id.content_frame, fragment)
                                                                                     .commit();


                                                                         } catch (Exception e) {
                                                                             //exception if it wants to show an message when the screen is dissmissed
                                                                             e.printStackTrace();
                                                                         }
                                                                         break;
                                                                     }


                                                                     default: {

                                                                         NetworkCallApiInterface service = MainActivity.retrofit.create(NetworkCallApiInterface.class);
                                                                         Call<EventModel> call = service.getEvent(result.get(position).getEventId(), MainActivity.token);
                                                                         call.enqueue(new Callback<EventModel>() {
                                                                             @Override
                                                                             public void onResponse(Call<EventModel> call, Response<EventModel> response) {
                                                                                 if (response.body() != null) {
                                                                                     MainActivity.createEventModel = null;
                                                                                     MainActivity.activeModel = null;

                                                                                     EventStaticFragment event = new EventStaticFragment();
                                                                                     event.setEventModel(response.body());
                                                                                     MainActivity.where_go_back = 3;

                                                                                     // Insert the fragment by replacing any existing fragment
                                                                                     android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                                                                     fragmentManager.beginTransaction()
                                                                                             .replace(R.id.content_frame, event)
                                                                                             .commit();
                                                                                 } else {
                                                                                     MainActivity.showAllert("Error", "The event is deleted");
                                                                                 }
                                                                             }

                                                                             @Override
                                                                             public void onFailure(Call<EventModel> call, Throwable t) {

                                                                             }
                                                                         });
                                                                         break;
                                                                     }
                                                                 }
                                                             }

                                                         }

                    );
            }
        }
    }

    public String getPath(Uri uri) {
        // just some safety built in
        if (uri == null) {
            // TODO perform some logging or show user feedback
            return "";
        }
        // try to retrieve the image from the media store first
        // this will only work for images selected from gallery
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getActivity().managedQuery(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        // this is our fallback here
        return uri.getPath();
    }


    private String getRealpath(final Intent data) {
        String realPath = "";
        // SDK < API11
        if (Build.VERSION.SDK_INT < 11)
            realPath = RealPathUtil.getRealPathFromURI_BelowAPI11(getActivity(), data.getData());

            // SDK >= 11 && SDK < 19
        else if (Build.VERSION.SDK_INT < 19)
            realPath = RealPathUtil.getRealPathFromURI_API11to18(getActivity(), data.getData());

            // SDK > 19 (Android 4.4)
        else
            realPath = RealPathUtil.getRealPathFromURI_API19(getActivity(), data.getData());

        return realPath;
    }

}
