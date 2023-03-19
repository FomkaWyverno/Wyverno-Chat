const chat = document.querySelector('.messages');
const viewers = document.querySelector('.viewers__count')
const status_stream = document.querySelector('.stream-status__status-text')

const start_block = document.querySelector('.start-block');

const start_button = document.querySelector('.start-block__container-btn__button');

start_button.addEventListener('click', () => {
    start_block.classList.add('hide');
});

const socket = new WebSocket('ws://localhost:2929')

socket.addEventListener('open', e => {
    console.log('WebSocket connection established.')
});

socket.addEventListener('message', e=> {
    const data = JSON.parse(e.data);

    console.log(data)

    if (data.type === 'messageHTML') {
        const messageElement = document.createElement('div');
        messageElement.classList.add('message');
        messageElement.innerHTML = data.content;
        chat.appendChild(messageElement);
        messageElement.scrollIntoView();
        deleteExtraElements(chat,200);
    } else if (data.type === 'reward_block') {
        const reward_block = document.createElement('div');
        reward_block.classList.add('reward-block')
        reward_block.innerHTML = data.content;
        chat.appendChild(reward_block);
        reward_block.scrollIntoView();
        deleteExtraElements(chat,200);
    } else if (data.type === 'reward_message') {
        const reward_message = document.createElement('div');
        reward_message.classList.add('reward-message')
        reward_message.innerHTML = data.content;
        chat.appendChild(reward_message);
        reward_message.scrollIntoView();
        deleteExtraElements(chat,200);
    } else if (data.type === 'videoPlayback') {
        const videoPlayback = JSON.parse(data.content);
        
        if (videoPlayback.type === 'VIEW_COUNT') {
            console.log('Viewers: ' + videoPlayback.content);
            viewers.textContent = videoPlayback.content;
            changeStreamStatus('STREAM_UP')
        } else if (videoPlayback.type === 'STREAM_UP') {
            console.log('Stream start!')
            changeStreamStatus(videoPlayback.type)
        } else if (videoPlayback.type === 'STREAM_DOWN') {
            console.log('Stream offline!')
            changeStreamStatus(videoPlayback.type)
        }
    }
})

function deleteExtraElements(element, maxElement) {
    while (element.children.length > maxElement) {
        console.log('Delete first element!');
        console.log(element.children[0]);
        element.children[0].remove();
    }
}

function changeStreamStatus(status) {
    if (status == 'STREAM_UP') {
        status_stream
        .classList
        .remove('stream-status__status-text--offline');
        status_stream
        .classList
        .add('stream-status__status-text--online');
        status_stream.textContent = 'Online'
    } else if ('STREAM_DOWN') {
        status_stream
        .classList
        .add('stream-status__status-text--offline');
        status_stream
        .classList
        .remove('stream-status__status-text--online');
        status_stream.textContent = 'Offilne';
        viewers.textContent = '-';
    } else {
        console.log('FAILED TYPE STATUS STREAM!')
    }
}