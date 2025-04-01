package com.e_dash;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Objects;

public class addProduct extends DialogFragment {


    private EditText productNameEditText;
    private EditText productQuantityEditText, product;
    private Button submitButton;

    public addProduct() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_product, container, false);

        productNameEditText = view.findViewById(R.id.productNameEditText);
        productQuantityEditText = view.findViewById(R.id.productQuantityEditText);
        product = view.findViewById(R.id.productPriceEditText);
        submitButton = view.findViewById(R.id.submitButton);

        submitButton.setOnClickListener(v -> {
            String productName = productNameEditText.getText().toString().trim();
            String productQuantity = productQuantityEditText.getText().toString().trim();
            String productPrice = product.getText().toString().trim();

            if (productName.isEmpty() || productQuantity.isEmpty()) {
                Toast.makeText(getActivity(), "Please enter all fields", Toast.LENGTH_SHORT).show();
            } else {
                int price = Integer.parseInt(productPrice);
                int quantity = Integer.parseInt(productQuantity);

                ((Monitor_Sales) requireActivity()).addProductToList(productName, price, quantity, 0);


                Toast.makeText(getActivity(), "Product Added: " + productName + " - Quantity: " + productQuantity, Toast.LENGTH_SHORT).show();
                dismiss();  // Close the dialog
            }
        });
        return view;
    }

    @Override
    public int getTheme() {
        return android.R.style.Theme_DeviceDefault_Light_Dialog_Alert;  // Makes it look like a pop-up
    }
}
