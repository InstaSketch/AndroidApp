package io.github.instasketch.instasketch.database;

import org.opencv.core.Mat;

/**
 * Created by transfusion on 16-2-20.
 */
public class Image {
    String _path;
    Mat _localStructuralDescriptor;
    String _localDescType;
    Mat _globalStructuralDescriptor;
    Mat _colorDescriptor;
    int _dateTaken;

    public Image(){

    }

    public Image(String path){
        this._path = path;
    }

    public Image(String path, int dateTaken, Mat localStructuralDescriptor, String localDescType, Mat globalStructuralDescriptor, Mat colorDescriptor){
        this._path = path;
        this._dateTaken = dateTaken;
        this._localStructuralDescriptor = localStructuralDescriptor;
        this._localDescType = localDescType;
        this._globalStructuralDescriptor = globalStructuralDescriptor;
        this._colorDescriptor = colorDescriptor;
    }

    public void setPath(String path){
        this._path = path;
    }

    public String getPath(){
        return this._path;
    }

    /*public void setLocalStructuralDescriptor(Mat localDesc, String descType){
        this._localDescType = descType;
        this._localStructuralDescriptor = localDesc;
    }*/

    public void setLocalStructuralDescriptor(Mat localDesc){
        this._localStructuralDescriptor = localDesc;
    }

    public Mat getLocalStructuralDescriptor(){
        return this._localStructuralDescriptor;
    }

    public String getLocalDescType(){
        return this._localDescType;
    }

    public void setGlobalStructuralDescriptor(Mat globalDesc){
        this._globalStructuralDescriptor = globalDesc;
    }

    public Mat getGlobalStructuralDescriptor(){
        return this._globalStructuralDescriptor;
    }

    public Mat getColorDescriptor(){
        return this._colorDescriptor;
    }

    public void setColorDescriptor(Mat colorDesc){
        this._colorDescriptor = colorDesc;
    }

    public int getDateTaken(){
        return this._dateTaken;
    }

    public void setDateTaken(int newDate){
        this._dateTaken = newDate;
    }
}
