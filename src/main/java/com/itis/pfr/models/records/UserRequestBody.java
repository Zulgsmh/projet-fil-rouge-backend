package com.itis.pfr.models.records;

public record UserRequestBody(String firstName, String lastName, String email, String password, String country) {}
