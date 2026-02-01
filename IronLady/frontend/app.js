(function(){
  const form = document.getElementById('quiz');
  const steps = Array.from(document.querySelectorAll('.step'));
  const progressBar = document.getElementById('progress-bar');
  const result = document.getElementById('result');
  const courseName = document.getElementById('courseName');
  const reason = document.getElementById('reason');
  const restartBtn = document.getElementById('restart');
  const tipsList = document.getElementById('tips');
  const loader = document.getElementById('loader');
  const exploreLink = document.getElementById('explore');

  const COURSE_URLS = {
  'Leadership Essentials Program': 'https://iamironlady.com/individualPrograms/Leadership_Essentials_Program',
  'Master of Business Warfare': 'https://iamironlady.com/individualPrograms/Master_of_Business_Warfare',
  '1-Crore Club': 'https://crclub.iamironlady.com/',
  '100 Board Members': 'https://iamironlady.com/individualPrograms/100_Board_Members_Program'
};

  const state = { careerStage: null, goal: null, challenge: null };
  const totalSteps = steps.length;

  function setProgress(stepIndex) {
    const percent = Math.round(((stepIndex + 1) / totalSteps) * 100);
    progressBar.style.width = percent + '%';
  }

  function showStep(index) {
    steps.forEach((s, i) => s.classList.toggle('active', i === index));
    setProgress(index);
  }

  function selectOption(sectionEl, value) {
    sectionEl.querySelectorAll('.card').forEach(el => el.classList.remove('selected'));
    const btn = sectionEl.querySelector(`.card[data-value="${value}"]`);
    if (btn) btn.classList.add('selected');
  }

  function enableNext(sectionEl) {
    const nextBtn = sectionEl.querySelector('.next');
    if (nextBtn) nextBtn.disabled = false;
    const submitBtn = sectionEl.querySelector('.submit');
    if (submitBtn) submitBtn.disabled = false;
  }

  function autoAdvance(sectionEl) {
    const idx = steps.indexOf(sectionEl);
    const nextIdx = idx + 1;
    if (nextIdx < steps.length) setTimeout(() => showStep(nextIdx), 200);
  }

  async function typeText(el, text) {
    el.textContent = '';
    const chars = Array.from(text || '');
    for (let i = 0; i < chars.length; i++) {
      el.textContent += chars[i];
      await new Promise(r => setTimeout(r, text && text.length > 100 ? 8 : 15));
    }
  }

  steps.forEach(section => {
    section.addEventListener('click', (e) => {
      const target = e.target.closest('.card');
      if (!target) return;
      const value = target.getAttribute('data-value');
      const stepNum = Number(section.getAttribute('data-step'));
      if (stepNum === 1) state.careerStage = value;
      if (stepNum === 2) state.goal = value;
      if (stepNum === 3) state.challenge = value;
      selectOption(section, value);
      enableNext(section);
      if (stepNum < 3) autoAdvance(section);
    });
  });

  document.getElementById('next-1').addEventListener('click', () => showStep(1));
  document.getElementById('next-2').addEventListener('click', () => showStep(2));
  document.querySelectorAll('.back').forEach(btn => btn.addEventListener('click', () => {
    const current = steps.findIndex(s => s.classList.contains('active'));
    showStep(Math.max(0, current - 1));
  }));

  form.addEventListener('submit', async (e) => {
    e.preventDefault();
    if (!state.careerStage || !state.goal || !state.challenge) return;

    try {
      if (loader) loader.classList.remove('hidden');
      result.classList.add('hidden');
      const res = await fetch('http://localhost:8080/api/recommend-course', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(state)
      });
      if (!res.ok) throw new Error('Request failed');
      const data = await res.json();
      courseName.textContent = data.courseName;
      if (tipsList) {
        tipsList.innerHTML = '';
        if (Array.isArray(data.tips)) {
          data.tips.forEach(t => {
            const li = document.createElement('li');
            li.textContent = t;
            tipsList.appendChild(li);
          });
        }
      }
      if (exploreLink) {
        const url = COURSE_URLS[data.courseName] || '#';
        exploreLink.setAttribute('href', url);
        if (url === '#') {
          exploreLink.classList.add('disabled');
          exploreLink.setAttribute('aria-disabled', 'true');
          exploreLink.removeAttribute('target');
          exploreLink.removeAttribute('rel');
        } else {
          exploreLink.classList.remove('disabled');
          exploreLink.removeAttribute('aria-disabled');
          exploreLink.setAttribute('target', '_blank');
          exploreLink.setAttribute('rel', 'noopener');
        }
      }
      await typeText(reason, data.reason || '');
      result.classList.remove('hidden');
      requestAnimationFrame(() => result.classList.add('show'));
      window.scrollTo({ top: document.body.scrollHeight, behavior: 'smooth' });
    } catch (err) {
      courseName.textContent = 'Something went wrong';
      reason.textContent = 'Please ensure the backend is running at http://localhost:8080 and try again.';
      result.classList.remove('hidden');
      requestAnimationFrame(() => result.classList.add('show'));
    } finally {
      if (loader) loader.classList.add('hidden');
    }
  });

  restartBtn.addEventListener('click', () => {
    Object.keys(state).forEach(k => state[k] = null);
    document.querySelectorAll('.card').forEach(el => el.classList.remove('selected'));
    document.querySelectorAll('.next').forEach(el => el.disabled = true);
    document.querySelectorAll('.submit').forEach(el => el.disabled = true);
    result.classList.remove('show');
    setTimeout(() => result.classList.add('hidden'), 300);
    reason.textContent = '';
    if (tipsList) tipsList.innerHTML = '';
    if (exploreLink) {
      exploreLink.setAttribute('href', '#');
      exploreLink.classList.add('disabled');
      exploreLink.setAttribute('aria-disabled', 'true');
      exploreLink.removeAttribute('target');
      exploreLink.removeAttribute('rel');
    }
    showStep(0);
    window.scrollTo({ top: 0, behavior: 'smooth' });
  });

  setProgress(0);
})();
