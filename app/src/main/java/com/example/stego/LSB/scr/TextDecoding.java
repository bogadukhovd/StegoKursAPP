package com.example.stego.LSB.scr;
import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import com.example.stego.LSB.Asynk.DecCallback;
import com.example.stego.LSB.Util.Utility;

import java.util.List;

/**
 * In this class all those method in EncodeDecode class are used to decode secret message in image.
 * All the tasks will run in background.
 */
public class TextDecoding extends AsyncTask<Steganography, Void, Steganography> {

    //Tag for Log
    private final static String TAG = TextDecoding.class.getName();

    private final Steganography result;
    //Callback interface for AsyncTask
    private final DecCallback textDecodingCallback;
    private ProgressDialog progressDialog;

    public TextDecoding(Activity activity, DecCallback textDecodingCallback) {
        super();
        this.progressDialog = new ProgressDialog(activity);
        this.textDecodingCallback = textDecodingCallback;
        //making result object
        this.result = new Steganography();
    }

    //setting progress dialog if wanted
    public void setProgressDialog(ProgressDialog progressDialog) {
        this.progressDialog = progressDialog;
    }

    //pre execution of method
    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        //setting parameters of progress dialog
        if (progressDialog != null) {
            progressDialog.setMessage("Loading, Please Wait...");
            progressDialog.setTitle("Decoding Message");
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.show();

        }
    }

    @Override
    protected void onPostExecute(Steganography imageSteganography) {
        super.onPostExecute(imageSteganography);

        //dismiss progress dialog
        if (progressDialog != null)
            progressDialog.dismiss();

        //sending result to callback
        textDecodingCallback.onCompleteTextDecoding(result);
    }

    @Override
    protected Steganography doInBackground(Steganography... imageSteganographies) {

        //If it is not already decoded
        if (imageSteganographies.length > 0) {

            Steganography imageSteganography = imageSteganographies[0];

            //getting bitmap image from file
            Bitmap bitmap = imageSteganography.getImage();

            //return null if bitmap is null
//            if (bitmap == null)
//                return null;

            //splitting images
            List<Bitmap> srcEncodedList = Utility.splitImage(bitmap);

            //decoding encrypted zipped message
            String decoded_message = EncodeDecode.decodeMessage(srcEncodedList);

            Log.d(TAG, "Decoded_Message : " + decoded_message);

            //text decoded = true
            if (!Utility.isStringEmpty(decoded_message)) {
                result.setDecoded(true);
            }

            //decrypting the encoded message
            String decrypted_message = Steganography.decryptMessage(decoded_message, imageSteganography.getSecret_key());
            Log.d(TAG, "Decrypted message : " + decrypted_message);

            //If decrypted_message is null it means that the secret key is wrong otherwise secret key is right.
            if (!Utility.isStringEmpty(decrypted_message)) {

                //secret key provided is right
                result.setSecretKeyWrong(false);

                // Set Results

                result.setMessage(decrypted_message);


                //free memory
                for (Bitmap bitm : srcEncodedList)
                    bitm.recycle();

                //Java Garbage Collector
                System.gc();
            }
        }

        return result;
    }
}