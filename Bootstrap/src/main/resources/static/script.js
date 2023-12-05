document.addEventListener('DOMContentLoaded', function() {
    const registeredRadio = document.getElementById('registered');
    const unregisteredRadio = document.getElementById('unregistered');
    const usernameInputGroup = document.getElementById('usernameInputGroup');
    const usernameInput = document.getElementById('username');
    const submitButton = document.getElementById('submitButton');

    registeredRadio.addEventListener('change', function() {
        if (this.checked) {
            usernameInputGroup.style.display = 'block';
            usernameInput.focus();
        } else {
            usernameInputGroup.style.display = 'none';
        }
    });

    unregisteredRadio.addEventListener('change', function() {
        if (this.checked) {
            usernameInputGroup.style.display = 'none';
        }
    });

    submitButton.addEventListener('click', function() {
        if (registeredRadio.checked && !usernameInput.value.trim()) {
            alert('Please enter your username.');
            usernameInput.focus();
        } else {
            alert('Form submitted!');
        }
    });
});
