console.log("finally i have built it")

// MutationObserver -> provides ability to watch changes being made to the webpage 
// Mutations -> list of changes that have occured on a webpage

function createAIButton(){

    const button = document.createElement('div');
    button.className = "T-I J-J5-Ji aoO v7 T-I-atl L3";
    button.style.marginRight= '8px';
    button.innerHTML = "AI reply";
    button.setAttribute('role', 'button');
    button.setAttribute('data-tooltip', 'Generate AI reply');
    return button;

}

function findComposeToolbar(){
    const selectors = [
        '.btC',
        '.aDh',
        '[role="toolbar"]',
        '.gU.Up'
    ];

    for(const selector of selectors){
        console.log(`Checking selector: ${selector}`); // Debugging log
        const toolbar = document.querySelector(selector);
        if(toolbar) return toolbar;
        return null;
    }
}

function getEmailContent(){
    const selectors = [
        '.h7',
        '.a3s.aiL',
        '[role="presentation"]',
        '.gmail_quote'
    ];

    for(const selector of selectors){
        console.log(`Checking selector: ${selector}`); // Debugging log
        const content = document.querySelector(selector);
        if(content) return content.innerText.trim();
        return '';
    }
}


function injectButton(){

    const existingButton = document.querySelector('.ai-reply-button');
    if(existingButton) existingButton.remove();

    const toolbar = findComposeToolbar();

    if(!toolbar){ console.log("toolbar not found"); return;}
    console.log("console found, creating AI button");

    button = createAIButton();
    button.classList.add('.ai-reply-button');

    button.addEventListener('click', async ()=> {

        try {
            button.innerHTML= 'Generating...';
            button.disabled= true;

            const emailContent = getEmailContent();

           const response = await fetch('http://localhost:8080/api/email/generate', {
                method: 'POST',
                headers: {
                    'content-type': 'application/json',
                },
                body: JSON.stringify({
                    emailContent: emailContent,
                    tone: "professional"
                })
            });

            if(!response.ok){
                throw new Error('API Request Failed');
            }

           const generatedReply = await response.text();
           const composeBox = document.querySelector('[role= "textbox"][g_editable="true"]');
           if(composeBox){
                composeBox.focus();
                document.execCommand('InsertText', false, generatedReply);
           } else console.log("compose box was not found");    
        } catch (error) {
            console.error(error);
            alert("failed to generate reply");
        } finally{
            button.innerHTML= "AI Reply";
            button.disabled = false;
        }

    });

    toolbar.insertBefore(button, toolbar.firstChild);   

}

const observer = new MutationObserver((mutations) => {
    for(const mutation of mutations){
        const addedNodes = Array.from(mutation.addedNodes);
        const hasComposeElements = addedNodes.some(node =>
            node.nodeType === node.ELEMENT_NODE &&
            (node.matches('.aDh, .btC, [role= "dialog"]') || node.querySelector('.aDh, .btC , [role= "dialog"]'))
        );

        if(hasComposeElements){
            console.log("composed window detected");
            setTimeout(injectButton, 500);
        }

    }
});

observer.observe(document.body, {
    childList: true,
    subtree: true
});