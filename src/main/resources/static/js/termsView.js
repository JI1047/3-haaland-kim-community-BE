document.addEventListener("DOMContentLoaded", () => {

  const agreeTerms = document.getElementById("agreeTerms");
  const agreePrivacy = document.getElementById("agreePrivacy");
  const nextBtn = document.getElementById("nextBtn");

  nextBtn.addEventListener("click", () => {

    const terms = {
      agreeTerms: agreeTerms.checked,
      agreePrivacy: agreePrivacy.checked,
      agreeTime: new Date().toISOString(),
    };

    document.cookie = `termsAgreement=${encodeURIComponent(JSON.stringify(terms))}; path=/; max-age=3600; SameSite=Lax`;

    window.location.href = `${FRONTEND_URL}/signup-input`;
  });
});
