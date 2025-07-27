document.addEventListener('DOMContentLoaded', function() {
    console.log("Quiz website script loaded.");

    // Login form validation (from previous version)
    const loginForm = document.querySelector('.login-container form');
    if (loginForm) {
        loginForm.addEventListener('submit', function(event) {
            const username = loginForm.querySelector('#username');
            const password = loginForm.querySelector('#password');
            if (username && username.value.trim() === '') {
                alert('Username cannot be empty.');
                event.preventDefault();
                return;
            }
            if (password && password.value.trim() === '') {
                alert('Password cannot be empty.');
                event.preventDefault();
                return;
            }
        });
    }

    // Quiz Timer Logic
    const timeDisplay = document.getElementById('time');
    const quizForm = document.getElementById('quizForm');

    // Check if quizDurationMinutes is defined (passed from takeQuiz.jsp)
    // This means 'quizDurationMinutes' must be a global JS variable set in a <script> tag in takeQuiz.jsp
    if (typeof quizDurationMinutes !== 'undefined' && quizDurationMinutes > 0 && timeDisplay && quizForm) {
        let timeLeft = quizDurationMinutes * 60; // Convert minutes to seconds

        function updateTimerDisplay() {
            const minutes = Math.floor(timeLeft / 60);
            let seconds = timeLeft % 60;
            seconds = seconds < 10 ? '0' + seconds : seconds; // Add leading zero if needed
            timeDisplay.textContent = `${minutes}:${seconds}`;
        }

        updateTimerDisplay(); // Initial display

        const timerInterval = setInterval(() => {
            timeLeft--;
            updateTimerDisplay();

            if (timeLeft <= 0) {
                clearInterval(timerInterval);
                timeDisplay.textContent = "Time's up!";
                alert("Time's up! The quiz will now be submitted.");
                quizForm.submit(); // Auto-submit the form
            }
        }, 1000);

        // Optional: Warn user before navigating away if quiz is in progress
        // window.addEventListener('beforeunload', function (e) {
        // if (timeLeft > 0 && !quizForm.submitted) { // Check a flag if form was submitted by user
        // e.preventDefault();
        // e.returnValue = 'Are you sure you want to leave? Your quiz progress might be lost.';
        //     }
        // });
    }
});