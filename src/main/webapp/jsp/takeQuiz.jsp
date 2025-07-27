<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:if test="${empty sessionScope.user}">
    <c:redirect url="${pageContext.request.contextPath}/login"/>
</c:if>
<c:if test="${empty quiz}">
    <c:redirect url="${pageContext.request.contextPath}/viewQuizzes?error=QuizNotLoaded"/>
</c:if>

<c:set var="pageTitle" value="Take Quiz: ${quiz.title}" scope="request"/>
<%@ include file="header.jsp" %>

<div class="container quiz-taking-container paginated-quiz-container">
    <div class="quiz-header-info">
        <h2><c:out value="${quiz.title}"/></h2>
        <p><c:out value="${quiz.description}"/></p>
        <div class="quiz-progress-bar-container">
            <div class="quiz-progress-bar" id="quizProgressBar" style="width: 0%;"></div>
        </div>
        <p id="questionCounter" class="question-counter-text">Question 1 of ${quiz.questions.size()}</p>
    </div>


    <c:if test="${quiz.durationMinutes > 0}">
        <div id="timer-display" class="timer-display-box">Time Remaining: <span id="time"></span></div>
    </c:if>

    <form id="quizForm" action="${pageContext.request.contextPath}/submitQuiz" method="post">
        <input type="hidden" name="quizId" value="${quiz.id}">

        <div id="questions-wrapper">
            <c:forEach var="question" items="${quiz.questions}" varStatus="loop">
                <div class="question-item-paginated" id="question-block-${loop.index}" style="display: ${loop.index == 0 ? 'block' : 'none'};">
                    <h3>Question ${loop.count}: <c:out value="${question.questionText}"/></h3>

                    <c:if test="${question.questionType == 'MCQ'}">
                        <ul class="options-list">
                            <c:forEach var="option" items="${question.options}">
                                <li>
                                    <input type="radio" id="option_${option.id}"
                                           name="question_${question.id}"
                                           value="${option.id}"
                                           data-question-index="${loop.index}" required>
                                    <label for="option_${option.id}"><c:out value="${option.optionText}"/></label>
                                </li>
                            </c:forEach>
                        </ul>
                    </c:if>
                    <%-- Add blocks here for other question types when implemented,
                         ensure they also have data-question-index --%>
                </div>
            </c:forEach>
        </div>

        <div class="quiz-navigation-buttons">
            <button type="button" id="prevQuestionBtn" class="btn btn-secondary" style="display: none;">Previous</button>
            <button type="button" id="nextQuestionBtn" class="btn btn-primary">Next Question</button>
            <button type="submit" id="submitQuizBtn" class="btn btn-success" style="display: none;">Submit Quiz</button>
        </div>
    </form>
</div>

<c:if test="${quiz.durationMinutes > 0}">
    <script>
        const quizDurationMinutes = ${quiz.durationMinutes};
    </script>
</c:if>
<script>
document.addEventListener('DOMContentLoaded', function() {
    console.log("Quiz script loaded. DOM fully parsed.");

    const questionsWrapper = document.getElementById('questions-wrapper');
    const questionBlocks = Array.from(questionsWrapper.querySelectorAll('.question-item-paginated'));
    const prevBtn = document.getElementById('prevQuestionBtn');
    const nextBtn = document.getElementById('nextQuestionBtn');
    const submitBtn = document.getElementById('submitQuizBtn');
    const quizForm = document.getElementById('quizForm');
    const progressBar = document.getElementById('quizProgressBar');
    const questionCounterText = document.getElementById('questionCounter');

    // Check if elements are found
    if (!questionsWrapper) console.error("Element with ID 'questions-wrapper' not found!");
    if (!prevBtn) console.error("Element with ID 'prevQuestionBtn' not found!");
    if (!nextBtn) console.error("Element with ID 'nextQuestionBtn' not found!");
    if (!submitBtn) console.error("Element with ID 'submitQuizBtn' not found!");


    let currentQuestionIndex = 0;
    const totalQuestions = questionBlocks.length;
    console.log("Total questions found:", totalQuestions);

    function showQuestion(index) {
        console.log("Showing question index:", index);
        questionBlocks.forEach((block, i) => {
            block.style.display = (i === index) ? 'block' : 'none';
        });
        updateNavigationButtons();
        updateProgressBar();
        updateQuestionCounter();
    }

    function updateNavigationButtons() {
        console.log("Updating navigation. Current index:", currentQuestionIndex, "Total questions:", totalQuestions);
        if (!prevBtn || !nextBtn || !submitBtn) {
            console.error("Critical: Navigation button elements are null in updateNavigationButtons!");
            return;
        }

        prevBtn.style.display = (currentQuestionIndex > 0) ? 'inline-block' : 'none';
        console.log("Prev button display:", prevBtn.style.display);

        if (totalQuestions > 0 && currentQuestionIndex === totalQuestions - 1) {
            nextBtn.style.display = 'none';
            submitBtn.style.display = 'inline-block';
        } else if (totalQuestions > 0) { // Ensure totalQuestions > 0 before setting nextBtn
            nextBtn.style.display = 'inline-block';
            submitBtn.style.display = 'none';
        } else { // No questions
            nextBtn.style.display = 'none';
            submitBtn.style.display = 'none';
        }
        console.log("Next button display:", nextBtn.style.display);
        console.log("Submit button display:", submitBtn.style.display);
    }

    function updateProgressBar() {
        if (progressBar && totalQuestions > 0) {
            const progressPercentage = ((currentQuestionIndex + 1) / totalQuestions) * 100;
            progressBar.style.width = progressPercentage + '%';
            // console.log("Progress bar width:", progressBar.style.width);
        }
    }

    function updateQuestionCounter() {
        if(questionCounterText && totalQuestions > 0) {
            questionCounterText.textContent = `Question ${currentQuestionIndex + 1} of ${totalQuestions}`;
        } else if (questionCounterText) {
            questionCounterText.textContent = "No questions in this quiz.";
        }
    }

    function isCurrentQuestionAnswered() {
        if (totalQuestions === 0 || currentQuestionIndex < 0 || currentQuestionIndex >= totalQuestions) return true; // No question to answer
        const currentBlock = questionBlocks[currentQuestionIndex];
        if (!currentBlock) {
            console.error("Current question block not found for answering check.");
            return true; // Avoid blocking if something is wrong
        }
        const radioButtons = currentBlock.querySelectorAll('input[type="radio"]');
        if (radioButtons.length > 0) {
            for (let radio of radioButtons) {
                if (radio.checked) return true;
            }
            console.log("No radio button checked for current question.");
            return false;
        }
        return true;
    }

    nextBtn.addEventListener('click', function() {
        console.log("Next button clicked.");
        if (!isCurrentQuestionAnswered()) {
            alert('Please answer the current question before proceeding.');
            return;
        }
        if (currentQuestionIndex < totalQuestions - 1) {
            currentQuestionIndex++;
            showQuestion(currentQuestionIndex);
        }
    });

    prevBtn.addEventListener('click', function() {
        console.log("Previous button clicked.");
        if (currentQuestionIndex > 0) {
            currentQuestionIndex--;
            showQuestion(currentQuestionIndex);
        }
    });

    // Timer Logic
    const timeDisplay = document.getElementById('time');
    if (typeof quizDurationMinutes !== 'undefined' && quizDurationMinutes > 0 && timeDisplay && quizForm) {
        // ... (timer logic remains the same) ...
        let timeLeft = quizDurationMinutes * 60;
        function updateTimerDisplay() {
            const minutes = Math.floor(timeLeft / 60);
            let seconds = timeLeft % 60;
            seconds = seconds < 10 ? '0' + seconds : seconds;
            if(timeDisplay) timeDisplay.textContent = `${minutes}:${seconds}`;
        }
        updateTimerDisplay();
        const timerInterval = setInterval(() => {
            timeLeft--;
            updateTimerDisplay();
            if (timeLeft <= 0) {
                clearInterval(timerInterval);
                if(timeDisplay) timeDisplay.textContent = "Time's up!";
                alert("Time's up! The quiz will now be submitted.");
                quizForm.submit();
            }
        }, 1000);
    } else {
        console.log("Timer not initialized. Duration:", typeof quizDurationMinutes !== 'undefined' ? quizDurationMinutes : 'undefined', "Time display found:", !!timeDisplay);
    }

    // Initial setup
    console.log("Performing initial setup. Total questions:", totalQuestions);
    if (totalQuestions > 0) {
        showQuestion(0);
    } else {
        updateNavigationButtons(); // Call this to ensure buttons are hidden if no questions
        updateQuestionCounter(); // Update counter for "no questions"
        console.log("No questions to display. Buttons should be hidden.");
    }
});
</script>

<%@ include file="footer.jsp" %>