document.addEventListener('DOMContentLoaded', () => {

    if (typeof dialogHandler !== 'undefined' && !dialogHandler.$dialog) {
        dialogHandler.$dialog = document.getElementById('dialog');
    }

    const switchButtons = document.querySelectorAll('.switch-btn');
    const forms = document.querySelectorAll('.form');
    const birthYearSelects = document.querySelectorAll('select[name="birthYear"]');
    const birthMonthSelects = document.querySelectorAll('select[name="birthMonth"]');
    const birthDaySelects = document.querySelectorAll('select[name="birthDay"]');
    const currentYear = new Date().getFullYear();

    birthYearSelects.forEach(select => {
        for (let i = currentYear; i >= 1950; i--) {
            const option = document.createElement('option');
            option.value = i;
            option.textContent = `${i}년`;
            select.append(option);
        }
    });

    birthMonthSelects.forEach(select => {
        for (let i = 1; i <= 12; i++) {
            const option = document.createElement('option');
            const monthValue = String(i).padStart(2, '0');
            option.value = monthValue;
            option.textContent = `${i}월`;
            select.append(option);
        }
    });

    birthDaySelects.forEach(select => {
        for (let i = 1; i <= 31; i++) {
            const option = document.createElement('option');
            const dayValue = String(i).padStart(2, '0');
            option.value = dayValue;
            option.textContent = `${i}일`;
            select.append(option);
        }
    });

    switchButtons.forEach(button => {
        button.addEventListener('click', () => {
            const type = button.getAttribute('data-type');

            switchButtons.forEach(btn => btn.classList.remove('active'));
            button.classList.add('active');

            forms.forEach(form => {
                form.removeAttribute('data-visible');
                form.scrollTop = 0;
            });

            const targetForm = document.querySelector(`.form[data-type="${type}"]`);
            if (targetForm) {
                targetForm.setAttribute('data-visible', '');
                targetForm.scrollTop = 0;
            }
        });
    });

    const defaultButton = document.querySelector('.switch-btn[data-type="common"]');
    if (defaultButton) {
        defaultButton.click();
    }

    const cancelButtons = document.querySelectorAll('button[name="cancel"]');
    cancelButtons.forEach(button => {
        button.addEventListener('click', () => {
            if (confirm('가입을 취소하시겠습니까? 입력한 정보는 저장되지 않습니다.')) {
                location.href = '/user/login';
            }
        });
    });

    const regexMap = {
        email: /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/,
        contact: /^010\d{8}$/,
        businessNumber: /^\d{10}$/,
        name: /^[가-힣]{2,5}$/,
        nickname: /^[a-zA-Z0-9가-힣\s]{2,15}$/,
        storeName: /^[a-zA-Z0-9가-힣\s]{2,15}$/,
        storeContact: /^0\d{8,10}$/,
        businessType: /^[가-힣]{2,10}$/,
        password: /^(?=.*[A-Za-z])(?=.*\d)(?=.*[$@$!%*#?&])[A-Za-z\d$@$!%*#?&]{8,}$/
    }

    const validateRules = {
        name: {
            regex: regexMap.name,
            empty: "성함을 입력해 주세요",
            invalid: "2 ~ 5자 한글 실명을 입력해 주세요"
        },
        email: {
            regex: regexMap.email,
            empty: "이메일을 입력해 주세요",
            invalid: "올바른 이메일 형식이 아닙니다. 유효한 이메일 형식을 사용해 주세요"
        },
        password: {
            regex: regexMap.password,
            empty: "비밀번호를 입력해 주세요",
            invalid: "8글자 이상 숫자, 영문자와 특수기호 하나를 혼합하여 입력해 주세요"
        },
        businessNumber: {
            regex: regexMap.businessNumber,
            empty: "사업자 등록 번호를 입력해 주세요",
            invalid: "숫자 10자리를 정확히 입력해 주세요"
        },
        nickname: {
            regex: regexMap.nickname,
            empty: "닉네임을 입력해 주세요",
            invalid: "특수문자를 제외하고 2 ~ 15자 이내로 입력해 주세요"
        },
        storeName: {
            regex: regexMap.storeName,
            empty: "가게 이름을 입력해 주세요",
            invalid: "특수문자를 제외하고 2 ~ 15자 이내로 입력해 주세요"
        },
        contact: {
            regex: regexMap.contact,
            empty: "휴대폰 번호를 입력해 주세요",
            invalid: "'-' 없이 010으로 시작하는 11자리 숫자를 입력해 주세요"
        },
        storeContact: {
            regex: regexMap.storeContact,
            empty: "가게 전화번호를 입력해 주세요",
            invalid: "올바른 전화번호 형식이 아닙니다 (예: 021234567)"
        },
        businessType: {
            regex: regexMap.businessType,
            empty: "업종을 입력해 주세요",
            invalid: "한글 2 ~ 10자 이내로 입력해 주세요 (예: 음식점, 카페)"
        }
    }

    const handleInputInvalidation = (e) => {
        const warningInput = e.target;
        const warningRule = validateRules[warningInput.name];
        if (!warningRule) return;

        const warningLabel = warningInput.closest('.label');
        const warningValue = warningInput.value.trim();

        if (warningValue === "") {
            toggleMessage(warningLabel, false)
        } else if (warningRule.regex && !warningRule.regex.test(warningValue)) {
            toggleMessage(warningLabel, true, warningRule.invalid);
        } else {
            toggleMessage(warningLabel, false);
        }
    }

    document.querySelectorAll('.field').forEach(field => {
        field.addEventListener('input', handleInputInvalidation);
    });

    document.querySelectorAll('input[name="passwordCheck"]').forEach(checkInput => {
        checkInput.addEventListener('input', (e) => {
            const form = e.target.closest('.form');
            const passwordValue = form['password'].value;
            const label = e.target.closest('.label');
            const isMismatch = (passwordValue !== e.target.value) && (e.target.value !== "");
            toggleMessage(label, isMismatch, "비밀번호가 일치하지 않습니다.")
        });
    });

    forms.forEach(form => {
        form.addEventListener('submit', (e) => {
            e.preventDefault();
            let firstErrorField = null;

            // 1. 유효성 검사 로직
            const inputs = form.querySelectorAll('.field[required], .field[name]');
            inputs.forEach(input => {
                const rule = validateRules[input.name];
                const label = input.closest('.label');
                const value = input.value.trim();
                if (!label) return;

                if (value === "") {
                    toggleMessage(label, true, rule?.empty || "필수 항목입니다.");
                    if (!firstErrorField) firstErrorField = input;
                } else if (rule && rule.regex && !rule.regex.test(value)) {
                    toggleMessage(label, true, rule.invalid);
                    if (!firstErrorField) firstErrorField = input;
                }
            });

            // 비밀번호 일치 확인
            const password = form['password'];
            const passwordCheck = form['passwordCheck'];
            if (password && passwordCheck && password.value !== passwordCheck.value) {
                toggleMessage(passwordCheck.closest('.label'), true, "비밀번호가 일치하지 않습니다.");
                if (!firstErrorField) firstErrorField = passwordCheck;
            }

            // 약관 동의 확인
            const requiredTerms = form.querySelectorAll('.term input[required]');
            const allChecked = Array.from(requiredTerms).every(checkBox => checkBox.checked);
            if (!allChecked) {
                dialogHandler.simpleYesModal('경고', '모든 필수 약관에 동의해 주세요.');
                return;
            }

            if (firstErrorField) {
                firstErrorField.focus();
                return;
            }

            // 2. FormData 생성 (여기서 모든 데이터를 모읍니다)
            const formData = new FormData(form); // 현재 이벤트가 발생한 '그 폼'의 데이터를 가져옴

            // 이메일 인증 토큰 (폼 내부에 해당 input이 있다고 가정)
            const $codeInput = form['code'];
            const $saltInput = form['salt'];
            if ($saltInput && $saltInput.value === "") {
                dialogHandler.simpleYesModal('경고', '이메일 인증을 완료해 주세요.');
                return;
            }

            // 생년월일 조합
            const year = form['birthYear']?.value;
            const month = form['birthMonth']?.value;
            const day = form['birthDay']?.value;
            if (year && month && day) {
                formData.set('birth', `${year}-${month}-${day}`);
            }

            // 마케팅 수신 동의
            const marketingCheckbox = form.querySelector('input[name="termMarketing"]');
            formData.set('termMarketingAgreed', marketingCheckbox && marketingCheckbox.checked ? 'true' : 'false');

            // 사장님 여부 및 전용 필드
            const isBoss = form.getAttribute('data-type') === 'boss';
            formData.set('isBoss', String(isBoss));

            if (isBoss) {
                // 파일은 name 속성으로 자동 담기지만, 명시적으로 다시 한번 확인
                const licenseFile = form.querySelector('input[name="licenseFile"]')?.files[0];
                const reportFile = form.querySelector('input[name="reportCardFile"]')?.files[0];
                if (licenseFile) formData.set('licenseFile', licenseFile);
                if (reportFile) formData.set('reportCardFile', reportFile);
            }

            // 3. 데이터를 들고 handleRegister 호출
            handleRegister(form, formData);
        });
    });

    const handleRegister = ($form, formData) => {
        // 이제 formData는 위에서 만든 완성된 데이터를 인자로 받습니다.
        const $emailInput = $form['email'];
        const $nicknameInput = $form['nickname'];

        fetch('/user/register', {
            method: 'POST',
            body: formData // 인자로 받은 formData를 그대로 전송
        }).then(response => {
            if (!response.ok) throw new Error(response.status.toString());
            return response.json();
        }).then(data => {
            switch (data.result) {
                case 'SUCCESS':
                    dialogHandler.simpleYesModal('알림', '회원 가입이 완료되었습니다!', {
                        onclick: () => location.href = '/user/login'
                    });
                    break;
                case 'FAILURE_DUPLICATE_EMAIL':
                    dialogHandler.simpleYesModal('경고', `이미 사용 중인 이메일입니다.`);
                    break;
                case 'FAILURE_DUPLICATE_NICKNAME':
                    dialogHandler.simpleYesModal('경고', `이미 사용 중인 닉네임입니다.`);
                    break;
                case 'FAILURE':
                    dialogHandler.simpleYesModal('경고', '가입 처리 중 오류가 발생했습니다.');
                    break;
                default:
                    dialogHandler.simpleYesModal('경고', '서버 응답 오류');
            }
        }).catch(error => {
            dialogHandler.simpleYesModal('오류', `서버 통신 문제 (${error})`);
        });
    }

    const addressButtons = document.querySelectorAll('button[name="addressButton"]');

    addressButtons.forEach(button => {
        button.addEventListener('click', () => {
            const currentForm = button.closest('.form');
            const addressInput = currentForm.querySelector('input[name="addressPrimary"]');
            const detailInput = currentForm.querySelector('input[name="addressSecondary"]');

            new daum.Postcode({
                oncomplete: function (data) {
                    let addr = '';
                    let extraAddr = '';

                    if (data.userSelectedType === 'R') {
                        addr = data.roadAddress;
                    } else {
                        addr = data.jibunAddress;
                    }

                    addressInput.value = addr;
                    toggleMessage(addressInput.closest('.label'), false);

                    if (detailInput) {
                        detailInput.focus();
                    }
                }
            }).open();
        });
    });

    document.querySelectorAll('button[name="codeSendButton"]').forEach($sendButton => {
        $sendButton.addEventListener('click', () => {
            const $currentForm = $sendButton.closest('.form');
            const $emailInput = $currentForm.querySelector('input[name="email"]');
            const $codeSendButton = document.querySelectorAll('button[name="codeSendButton"]');
            // const emailLabel = $emailInput.closest('.label');

            if ($emailInput.value === '') {
                dialogHandler.simpleYesModal('경고', '이메일을 입력해 주세요.');
                $emailInput.focus();
                return;
            }
            const emailRegex = /^(?=.{8,50}$)([\da-zA-Z_.]{4,25})@([\da-z\-]+\.)?([\da-z\-]{2,})\.([a-z]{2,15}\.)?([a-z]{2,3})$/;
            if (!emailRegex.test($emailInput.value)) {
                dialogHandler.simpleYesModal('경고', '올바른 이메일 형식을 입력해 주세요.');
                $emailInput.focus();
                return;
            }

            const xhr = new XMLHttpRequest();
            const formData = new FormData();
            formData.append('email', $emailInput.value);
            xhr.onreadystatechange = () => {
                if (xhr.readyState !== XMLHttpRequest.DONE) {
                    return;
                }
                if (xhr.status < 200 || xhr.status >= 400) {
                    dialogHandler.simpleYesModal('오류', `요청을 전송하는 도중 오류가 발생했습니다. 잠시 후 다시 시도해 주세요. (${xhr.status})`);
                    return;
                }
                const response = JSON.parse(xhr.responseText);
                switch (response.result) {
                    case 'FAILURE':
                        dialogHandler.simpleYesModal('경고', '알 수 없는 이유로 이메일을 전송하지 못했습니다. 잠시 후 다시 시도해 주세요');
                        break;
                    case 'FAILURE_EMAIL_DUPLICATE':
                        dialogHandler.simpleYesModal('경고', `입력하신 이메일(${$emailInput.value}) 은/는 이미 사용 중입니다.`);
                        break;
                    case 'SUCCESS':
                        $currentForm.querySelector('input[name="salt"]').value = response['salt'];
                        dialogHandler.simpleYesModal('알림', `입력하신 이메일(${$emailInput.value})로 인증번호를 전송하였습니다. 인증번호는 3분간만 유효하니 유의해 주세요.`)
                        break;
                    default:
                        dialogHandler.simpleYesModal('경고', '서버가 알 수 없는 응답을 반환하였습니다. 잠시 후 다시 시도해 주세요.');
                }
            };
            xhr.open('POST', '/user/email');
            xhr.send(formData);
        });
    });

    document.querySelectorAll('button[name="codeVerifyButton"]').forEach($verifyButton => {
        $verifyButton.addEventListener('click', () => {
            const $currentForm = $verifyButton.closest('.form');
            const $emailInput = $currentForm.querySelector('input[name="email"]');
            const $codeInput = $currentForm.querySelector('input[name="code"]');
            const $saltInput = $currentForm.querySelector('input[name="salt"]');

            if ($codeInput.value === '') {
                dialogHandler.simpleYesModal('경고', '이메일 인증번호를 입력해 주세요');
                $codeInput.focus();
                return;
            }

            if (!/^(\d{6})$/.test($codeInput.value)) {
                dialogHandler.simpleYesModal('경고', '올바른 이메일 인증번호를 입력해 주세요');
                $codeInput.focus();
                $codeInput.select();
                return;
            }

            const xhr = new XMLHttpRequest();
            const formData = new FormData();
            formData.append('email', $emailInput.value);
            formData.append('code', $codeInput.value);
            formData.append('salt', $saltInput.value);
            xhr.onreadystatechange = () => {
                if (xhr.readyState !== XMLHttpRequest.DONE) {
                    return;
                }
                if (xhr.status < 200 || xhr.status >= 400) {
                    dialogHandler.simpleYesModal('오류', `요청을 전송하는 도중 오류가 발생했습니다. 잠시 후 다시 시도해 주세요. (${xhr.status})`);
                    return;
                }
                const response = JSON.parse(xhr.responseText);
                switch (response.result) {
                    case 'FAILURE':
                        dialogHandler.simpleYesModal('경고', '인증번호가 올바르지 않습니다. 다시 확인해 주세요.');
                        break;
                    case 'FAILURE_EXPIRED':
                        dialogHandler.simpleYesModal('경고', '인증 세션이 만료되었습니다. 인증번호를 다시 요청해 주세요.');
                        $emailInput.focus();
                        break;
                    case 'SUCCESS':
                        $currentForm.querySelector('input[name="salt"]').value = response['salt'];
                        dialogHandler.simpleYesModal('알림', '인증 완료되었습니다.')
                        break;
                    default:
                        dialogHandler.simpleYesModal('경고', '서버가 알 수 없는 응답을 반환하였습니다. 잠시 후 다시 시도해 주세요.');
                }
            };
            xhr.open('PATCH', '/user/email');
            xhr.send(formData);
        });
    });

    let isBusinessVerified = false;

    const $businessCheckButton = document.querySelector('button[name="businessCheckButton"]')
    $businessCheckButton.addEventListener('click', () => {
        const $businessNumberInput = document.querySelector('input[name="businessNumber"]');
        const number = $businessNumberInput.value;
        fetch(`/user/verify-business?businessNumber=${number}`)
            .then(response => response.json())
            .then(data => {
                if (data.result === 'SUCCESS') {
                    dialogHandler.simpleYesModal('알림', '인증되었습니다.');
                    isBusinessVerified = true;
                    $businessNumberInput.readOnly = true;
                } else {
                    dialogHandler.simpleYesModal('경고', '유효하지 않은 사업자 번호입니다.');
                    isBusinessVerified = false;
                }
            });
    });

    document.querySelectorAll('button[name="nicknameCheck"]').forEach($checkButton => {
        $checkButton.addEventListener('click', () => {
            const $currentForm = $checkButton.closest('.form');
            const $nicknameInput = $currentForm.querySelector('input[name="nickname"]');
            const $nicknameLabel = $nicknameInput.closest('.label');

            if ($nicknameInput.value === '') {
                dialogHandler.simpleYesModal('경고', '닉네임을 입력해 주세요');
                $nicknameInput.focus();
                return;
            }

            const nicknameRegex = /^[a-zA-Z0-9가-힣\s]{2,15}$/;
            if (!nicknameRegex.test($nicknameInput.value)) {
                toggleMessage($nicknameLabel, true, '특수 문자를 제외하고 2 ~ 15자 이내로 입력해 주세요');
                $nicknameInput.focus();
                $nicknameInput.select();
                return;
            }

            const xhr = new XMLHttpRequest();
            const url = new URL(location.origin + '/user/nickname-status');
            url.searchParams.set('nickname', $nicknameInput.value);
            xhr.onreadystatechange = () => {
                if (xhr.readyState !== XMLHttpRequest.DONE) {
                    return;
                }
                if (xhr.status < 200 || xhr.status >= 400) {
                    dialogHandler.simpleYesModal('오류', `요청을 전송하는 도중 오류가 발생했습니다. (${xhr.status})`);
                    return;
                }
                const response = JSON.parse(xhr.responseText);
                switch (response.result) {
                    case 'FAILURE':
                        // 이미 사용 중인 경우
                        dialogHandler.simpleYesModal('경고', `입력하신 닉네임(${$nicknameInput.value})은 이미 사용 중입니다.`);
                        break;

                    case 'SUCCESS':
                        // 사용 가능한 경우
                        dialogHandler.simpleYesModal('알림', `입력하신 닉네임(${$nicknameInput.value})은 사용 가능합니다. 해당 닉네임을 사용할까요?`);
                        break;

                    default:
                        dialogHandler.simpleYesModal('경고', '서버가 알 수 없는 응답을 반환하였습니다.');
                }

            };
            xhr.open('GET', url);
            xhr.send();
        });
    });


    /**
     * @param {HTMLElement} labelElement
     * @param {boolean} isError
     * @param {string} message*/

    function toggleMessage(labelElement, isError, message = "") {
        const infoMessage = labelElement.querySelector('[data-message="info"]');
        const warningMessage = labelElement.querySelector('[data-message="warning"]');

        if (isError) {
            infoMessage?.removeAttribute('data-visible');
            warningMessage?.setAttribute('data-visible', '');

            const warningText = warningMessage?.querySelector('.text');
            if (warningText && message) {
                warningText.innerText = message;
            }
        } else {
            warningMessage?.removeAttribute('data-visible');
            infoMessage?.setAttribute('data-visible', '');
        }
    }
});
