function validateForm() {

    var pattern = /[a-zA-Z0-9-]+/;

    document.getElementById("username-error").textContent ="";
    document.getElementById("password-error").textContent ="";

    var name = document.loginForm.username.value;

    if (name.length > 30 || name.length < 3) {

        document.getElementById("username-error").
            textContent = "username should be 3 to 30 symbols in length";
        return false;
    }

    if (!pattern.test(name)) {

        document.getElementById("username-error").
            textContent = "username should contains only letters and numbers";
        return false;
    }

    var pass = document.loginForm.password.value;

    if (pass.length > 30 || pass.length < 7) {

        document.getElementById("password-error").
            textContent = "password should be 7 to 30 symbols in length";
        return false;
    }

    if (!pattern.test(name)) {

        document.getElementById("password-error").
            textContent = "password should contains only letters and numbers";
        return false;
    }

    return true;
}
