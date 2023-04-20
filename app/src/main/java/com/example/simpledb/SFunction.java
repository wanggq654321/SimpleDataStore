package com.example.simpledb;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

import android.os.Build;

import java.io.Serializable;
import java.util.function.Function;

import androidx.annotation.RequiresApi;

@RequiresApi(api = Build.VERSION_CODES.N)
@FunctionalInterface
public interface SFunction<T, R> extends Function<T, R>, Serializable {

}

