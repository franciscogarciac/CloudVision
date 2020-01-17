package com.example.practicavision;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.FaceAnnotation;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;
import com.google.api.services.vision.v1.model.TextAnnotation;

import org.apache.commons.io.output.ByteArrayOutputStream;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    public Vision vision;
    ImageView imagen ;
    static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imagen = (ImageView)findViewById(R.id.imageView2);
        Vision.Builder visionBuilder = new Vision.Builder(new NetHttpTransport(),
                new AndroidJsonFactory(),  null);
        visionBuilder.setVisionRequestInitializer(new VisionRequestInitializer("Api_Key"));
                 vision = visionBuilder.build();
    }

    public void Click(View v){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {

                BitmapDrawable drawable = (BitmapDrawable) imagen.getDrawable();
                Bitmap bitmap = drawable.getBitmap();

               bitmap = scaleBitmapDown(bitmap, 1200);

               ByteArrayOutputStream stream = new ByteArrayOutputStream();
               bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream);
               byte[] imageInByte = stream.toByteArray();

               //1.- Paso
                Image inputImage = new Image();
                inputImage.encodeContent(imageInByte);

                //2.-Feature
                Feature desiredFeature = new Feature();
                desiredFeature.setType("TEXT_DETECTION");

                //3.-Armo Solicitud
                AnnotateImageRequest request = new AnnotateImageRequest();
                request.setImage(inputImage);
                request.setFeatures(Arrays.asList(desiredFeature));
                BatchAnnotateImagesRequest batchRequest =  new BatchAnnotateImagesRequest();
                batchRequest.setRequests(Arrays.asList(request));

                //4. Asignamos al control VisionBuilder la solicitud
                try {
                    Vision.Images.Annotate annotateRequest  = vision.images().annotate(batchRequest);

                    //5.-Enviamos la solicitud
                    annotateRequest.setDisableGZipContent(true);
                    BatchAnnotateImagesResponse batchResponse  = annotateRequest.execute();

                    //6.-Obtener la respuesta
                    TextAnnotation text = batchResponse.getResponses().get(0).getFullTextAnnotation();



                    final String result=text.getText();
                    //7.-ASingnar la respuesta a la UI
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            TextView imageDetail = (TextView)findViewById(R.id.textView2);

                            imageDetail.setText(result);

                        }
                    });

                    // return text.getText();


                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });

    }
    public void ClickFace(View v){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {

                BitmapDrawable drawable = (BitmapDrawable) imagen.getDrawable();
                Bitmap bitmap = drawable.getBitmap();

                bitmap = scaleBitmapDown(bitmap, 1200);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream);
                byte[] imageInByte = stream.toByteArray();

                //1.- Paso
                Image inputImage = new Image();
                inputImage.encodeContent(imageInByte);

                //2.-Feature
                Feature desiredFeature = new Feature();
                desiredFeature.setType("FACE_DETECTION");

                //3.-Armo Solicitud
                AnnotateImageRequest request = new AnnotateImageRequest();
                request.setImage(inputImage);
                request.setFeatures(Arrays.asList(desiredFeature));
                BatchAnnotateImagesRequest batchRequest =  new BatchAnnotateImagesRequest();
                batchRequest.setRequests(Arrays.asList(request));

                //4. Asignamos al control VisionBuilder la solicitud
                try {
                    Vision.Images.Annotate annotateRequest  = vision.images().annotate(batchRequest);

                    //5.-Enviamos la solicitud
                    annotateRequest.setDisableGZipContent(true);
                    BatchAnnotateImagesResponse batchResponse  = annotateRequest.execute();

                    //6.-Obtener la respuesta


                    List<FaceAnnotation> faces = batchResponse.getResponses().get(0).getFaceAnnotations();
                    int numberOfFaces = faces.size();
                    String likelihoods = "";
                    for(int i=0; i<numberOfFaces; i++) {
                        likelihoods += "\n It is " +  faces.get(i).getJoyLikelihood() +
                                " that face " + i + " is happy";
                    }
                    final String message = "This photo has " + numberOfFaces + " faces" + likelihoods;


                    //7.-ASingnar la respuesta a la UI
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {


                            TextView faceimage=(TextView)findViewById(R.id.textView2);

                            faceimage.setText(message.toString());
                        }
                    });

                    // return text.getText();


                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
    }
    public void ClickLabel(View v){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {

                BitmapDrawable drawable = (BitmapDrawable) imagen.getDrawable();
                Bitmap bitmap = drawable.getBitmap();

                bitmap = scaleBitmapDown(bitmap, 1200);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream);
                byte[] imageInByte = stream.toByteArray();

                //1.- Paso
                Image inputImage = new Image();
                inputImage.encodeContent(imageInByte);

                //2.-Feature
                Feature desiredFeature = new Feature();
                desiredFeature.setType("LABEL_DETECTION");

                //3.-Armo Solicitud
                AnnotateImageRequest request = new AnnotateImageRequest();
                request.setImage(inputImage);
                request.setFeatures(Arrays.asList(desiredFeature));
                BatchAnnotateImagesRequest batchRequest =  new BatchAnnotateImagesRequest();
                batchRequest.setRequests(Arrays.asList(request));

                //4. Asignamos al control VisionBuilder la solicitud
                try {
                    Vision.Images.Annotate annotateRequest  = vision.images().annotate(batchRequest);

                    //5.-Enviamos la solicitud
                    annotateRequest.setDisableGZipContent(true);
                    BatchAnnotateImagesResponse batchResponse  = annotateRequest.execute();

                    //6.-Obtener la respuesta


                    StringBuilder message = new StringBuilder("I found these things:\n\n");
                    List<EntityAnnotation> labels = batchResponse.getResponses().get(0).getLabelAnnotations();
                    if (labels != null) {
                        for (EntityAnnotation label : labels) {
                            message.append(String.format(Locale.US, "%.3f: %s", label.getScore(), label.getDescription()));
                            message.append("\n");
                        }
                    } else {
                        message.append("nothing");
                    }
                    final String respuesta=message.toString();
                   // return message.toString();



                    //7.-ASingnar la respuesta a la UI
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {


                            TextView faceimage=(TextView)findViewById(R.id.textView2);

                            faceimage.setText(respuesta.toString());
                        }
                    });

                    // return text.getText();


                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
    }
    private Bitmap scaleBitmapDown(Bitmap bitmap, int maxDimension) {
        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();
        int resizedWidth = maxDimension;
        int resizedHeight = maxDimension;
        if (originalHeight > originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = (int) (resizedHeight * (float) originalWidth / (float) originalHeight);
        } else if (originalWidth > originalHeight) {
            resizedWidth = maxDimension;
            resizedHeight = (int) (resizedWidth * (float) originalHeight / (float) originalWidth);
        } else if (originalHeight == originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = maxDimension;
        }
        return Bitmap.createScaledBitmap(bitmap, resizedWidth, resizedHeight, false);
    }

    public void ClickBuscar(View v){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/");
        startActivityForResult(intent.createChooser(intent,"Seleccione la app"),10);
    }
    public void ClickFoto(View v){
        llamaritem();
    }
    private void llamaritem(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }

    }
    @Override
    protected void onActivityResult(int requestCode,int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(resultCode==RESULT_OK && requestCode==10){

                    Uri MIpath = data.getData();
                    imagen.setImageURI(MIpath);

        }else {
            if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                imagen.setImageBitmap(imageBitmap);
            }
        }
    }

}
