const collapse_button = document.querySelector('.list-control-panel__collapse');
const maximize_minimize_button = document.querySelector('.list-control-panel__maximize-minimize');
const close_button = document.querySelector('.list-control-panel__close');

collapse_button.addEventListener('click', () => { // Сворачиваем окно
    console.log('click collapse')
    ipcRenderer.send('window.main.collapse')
});
maximize_minimize_button.addEventListener('click', () => { // Маштабируем или минимизируем
    console.log('click maximize_minimize');
    ipcRenderer.send('window.main.maximize-minimize');
});
close_button.addEventListener('click', () => { // Закрываем окно
    console.log('click close');
    ipcRenderer.send('window.main.close')
});

ipcRenderer.on('window.main.maximize', () => {
    maximize_minimize_button.children[0].classList.add('minimize')
});

ipcRenderer.on('window.main.minimize', () => {
    maximize_minimize_button.children[0].classList.remove('minimize');
});