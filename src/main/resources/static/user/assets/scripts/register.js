document.addEventListener('DOMContentLoaded', () => {
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

            forms.forEach(form => {form.removeAttribute('data-visible');
                form.scrollTop = 0;});

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

            const password = form['password'];
            const passwordCheck = form['passwordCheck'];
            if (password && passwordCheck && password.value !== passwordCheck.value) {
                toggleMessage(passwordCheck.closest('.label'), true, "비밀번호가 일치하지 않습니다.");
                if (!firstErrorField) firstErrorField = passwordCheck;
            }

            const requiredTerms = form.querySelectorAll('.term input[required]');
            const allChecked = Array.from(requiredTerms).every(checkBox => checkBox.checked);
            if (!allChecked) {
                // alert('모든 필수 약관에 동의해 주세요.');
                dialogHandler.simpleYesModal('경고', '모든 필수 약관에 동의해 주세요.');
                return;
            }

            if (firstErrorField) {
                firstErrorField.focus();
                return;
            }

            const formData = new FormData(form);
            const year = form['birthYear'].value;
            const month = form['birthMonth'].value;
            const day = form['birthDay'].value;
            const marketingCheckbox = form.querySelector('input[name="termMarketingAgreed"]');
            const $emailValue = form.querySelector('input[name="email"]').value;
            const $emailCodeValue = form.querySelector('input[name="emailCode"]').value;
            const $emailSaltValue = form.querySelector('input[name="emailSalt"]').value;
            formData.append('type', form.getAttribute('data-type'));
            if (year && month && day) {
                formData.append('birth', `${year}-${month}-${day}`);
            }
            if (form.getAttribute('data-type') === 'boss') {
                const licenseFile = form.querySelector('input[name="businessLicense"]').files[0];
                const reportFile = form.querySelector('input[name="reportCardUrl"]').files[0];
                if(licenseFile) formData.append('businessLicense', licenseFile);
                if(reportFile) formData.append('reportCardUrl', reportFile);
            }
            formData.append('termMarketingAgreed', marketingCheckbox && marketingCheckbox.checked ? 'true' : 'false');
            formData.append('isBoss', String(form.getAttribute('data-type') === 'boss'));
            formData.set('email', $emailValue);
            formData.append('code', $emailCodeValue);
            formData.append('salt', $emailSaltValue);

            // if (typeof mtLoading !== 'undefined') {
            //     mtLoading.show();
            // }

            fetch('/user/register', {
                method: 'POST',
                body: formData
            }).then(response => {
                //mtLoading.hide();
                if (!response.ok) throw new Error(response.status.toString());
                return response.json();
            })
                .then(response => {
                    switch (response.result) {
                        case 'SUCCESS':
                            dialogHandler.simpleYesModal('알림', '회원 가입이 완료되었습니다!');
                            // alert('회원가입이 완료되었습니다!');
                            location.href = '/user/login';
                            break;
                        case 'FAILURE_DUPLICATE_EMAIL':
                            dialogHandler.simpleYesModal('경고', `입력하신 이메일(${$emailInput.value})은 이미 사용 중인 이메일입니다.`)
                            // alert('이미 사용 중인 이메일입니다.');
                            break;
                        case 'FAILURE':
                            dialogHandler.simpleYesModal('경고', '가입 처리 중 알 수 없는 오류가 발생했습니다. 잠시 후 다시 시도해 주세요.');
                            // alert('가입 처리 중 알 수 없는 오류가 발생했습니다.');
                            break;
                        default:
                            dialogHandler.simpleYesModal('오류', `서버 응답 중 오류가 생겼습니다. 잠시 후 다시 시도해 주세요. ${response.result})`);
                            // alert(`서버 응답 오류: ${response.result}`);
                    }
                })
                .catch(error => {
                    //mtLoading.hide();
                    dialogHandler.simpleYesModal('오류', `요청을 전송하는 도중 오류가 발생했습니다. (${error})`);
                    // alert(`요청을 전송하는 도중 오류가 발생했습니다. (${error})`);
                });
        });

    });

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

    document.querySelectorAll('button[name="emailCodeSendButton"]').forEach($sendButton => {
        $sendButton.addEventListener('click', () => {
            const $currentForm = $sendButton.closest('.form');
            const $emailInput = $currentForm.querySelector('input[name="email"]');
            const $emailCodeSendButton = document.querySelectorAll('button[name="emailCodeSendButton"]');
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
                        $currentForm.querySelector('input[name="emailSalt"]').value = response['salt'];
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

    document.querySelectorAll('button[name="emailCodeVerifyButton"]').forEach($verifyButton => {
        $verifyButton.addEventListener('click', () => {
            const $currentForm = $verifyButton.closest('.form');
            const $emailInput = $currentForm.querySelector('input[name="email"]');
            const $emailCodeInput = $currentForm.querySelector('input[name="emailCode"]');
            const $emailSaltInput = $currentForm.querySelector('input[name="emailSalt"]');

            if ($emailCodeInput.value === '') {
                dialogHandler.simpleYesModal('경고', '이메일 인증번호를 입력해 주세요');
                $emailCodeInput.focus();
                return;
            }

            if (!/^(\d{6})$/.test($emailCodeInput.value)){
                dialogHandler.simpleYesModal('경고', '올바른 이메일 인증번호를 입력해 주세요');
                $emailCodeInput.focus();
                $emailCodeInput.select();
                return;
            }

            const xhr = new XMLHttpRequest();
            const formData = new FormData();
            formData.append('email', $emailInput.value);
            formData.append('code', $emailCodeInput.value);
            formData.append('salt', $emailSaltInput.value);
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
                        $currentForm.querySelector('input[name="emailSalt"]').value = response['salt'];
                        break;
                    default:
                        dialogHandler.simpleYesModal('경고', '서버가 알 수 없는 응답을 반환하였습니다. 잠시 후 다시 시도해 주세요.');
                }
            };
            xhr.open('PATCH', '/user/email');
            xhr.send(formData);
        });
    });

    document.querySelectorAll('button[name="nicknameCheck"]').forEach($checkButton => {
        $checkButton.addEventListener('click', () => {
            const $currentForm = $checkButton.closest('.form');
            const $nicknameInput = $currentForm.querySelector('input[name="nickname"]');
            const $nicknameLabel = $nicknameInput.closest('.label');

            if ($nicknameInput.value === '') {
                toggleMessage($nicknameLabel, true, '닉네임을 입력해 주세요')
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
                        toggleMessage($nicknameLabel, true, `입력하신 닉네임(${$nicknameInput.value})은 이미 사용 중입니다.`);
                        break;

                    case 'SUCCESS':
                        // 사용 가능한 경우
                        if (confirm(`입력하신 닉네임(${$nicknameInput.value})은 사용 가능합니다. 해당 닉네임을 사용할까요?`)) {
                            toggleMessage($nicknameLabel, false);
                        }
                        break;

                    default:
                        dialogHandler.simpleYesModal('경고', '서버가 알 수 없는 응답을 반환하였습니다.');
                }

            };
            xhr.open('GET', url);
            xhr.send();
        });
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
