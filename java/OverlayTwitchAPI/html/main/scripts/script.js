const { ipcRenderer } = require('electron');

const loading = document.querySelector('.loading')
const loginButton = document.querySelector('.login');
const containerLogged = document.querySelector('.container__logged')
const openOverlay = document.querySelector('.open-overlay');

// Description Block
const channel_logo = document.querySelector('.channel-description__logo')
const channel_username = document.querySelector('.channel-description__username');
const channel_followers = document.querySelector('.channel-description__followers');

//loggin();

loginButton.addEventListener('click', () => {
    authorization();
});

openOverlay.addEventListener('click', () => {
    ipcRenderer.send('open-overlay');
});

ipcRenderer.on('logged', () => { loggin() });

function loggin() {

    loginButton.classList.add('hide')
    loading.classList.remove('hide');
    const xhr = new XMLHttpRequest()

    xhr.open('GET', '/logging');

    xhr.onload = () => {
        if (xhr.readyState === XMLHttpRequest.DONE) {
            if (xhr.status === 200) {
                console.log('AccessToken is valid!')
                const jsonResponse = JSON.parse(xhr.responseText);
                console.log(jsonResponse);
                loginButton.classList.add('hide') // Відключаємо кнопку авторизації
                loading.classList.add('hide') // Вимикаємо загрузку

                containerLogged.classList.remove('hide') // Відображуємо авторизоване вікно
                channel_logo.src = jsonResponse.profileImageURL;
                channel_username.textContent = jsonResponse.displayName;
                channel_followers.textContent = `${jsonResponse.countFollowers} followers`
            } else if (xhr.status === 401) {
                console.log('AccessToken is not valid!')
                loginButton.classList.remove('hide')
                loading.classList.add('hide')
            }
        }
    }

    xhr.send();
}

function authorization() {
    let xhr = new XMLHttpRequest();

    xhr.open('GET', '/authorization');

    xhr.onload = () => {
        if (xhr.readyState = XMLHttpRequest.DONE) {
            if (xhr.status === 200) {
                console.log(`Response: ${xhr.responseText}`);
                ipcRenderer.send('main-auth-url', {
                    code: 200,
                    url: xhr.responseText
                })
            }

        } else {
            console.log(`Error response: ${xhr.readyState}`)
        }
    }

    xhr.send();

    if (!isRunWaitAuth) {
        isRunWaitAuth = true;
        wait_authorization();
    }

}

let isRunWaitAuth = false;

function wait_authorization() {
    fetch('/authorization-status')
        .then(r => r.json())
        .then(isAuth => {
            if (isAuth) {
                isRunWaitAuth = false;
                loggin();
            } else {
                setTimeout(wait_authorization, 2000);
            }
        }).catch(error => {
            console.error(`Помилка: ${error}`)
        });

}