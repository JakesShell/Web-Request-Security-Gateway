const el = {
    readinessScore: document.getElementById("readinessScore"),
    rating: document.getElementById("rating"),
    summary: document.getElementById("summary"),
    totalRequests: document.getElementById("totalRequests"),
    allowedRequests: document.getElementById("allowedRequests"),
    blockedRequests: document.getElementById("blockedRequests"),
    openTickets: document.getElementById("openTickets"),
    eventList: document.getElementById("eventList"),
    rulesList: document.getElementById("rulesList"),
    headersList: document.getElementById("headersList"),
    eventSubtitle: document.getElementById("eventSubtitle"),
    aiSummary: document.getElementById("aiSummary"),
    ticketList: document.getElementById("ticketList"),
    trendBars: document.getElementById("trendBars"),
    siemExport: document.getElementById("siemExport"),
    cloudReadiness: document.getElementById("cloudReadiness"),
    wafComparison: document.getElementById("wafComparison"),
    kubernetesStatus: document.getElementById("kubernetesStatus"),
};

async function api(path, options = {}) {
    const response = await fetch(path, {
        headers: { "Content-Type": "application/json" },
        ...options,
    });

    const data = await response.json();

    if (!response.ok) {
        throw new Error(data.message || "Request failed");
    }

    return data;
}

function ratingClass(value) {
    if (value === "critical") return "risk-high";
    if (value === "watch" || value === "moderate") return "risk-medium";
    return "risk-low";
}

function severityClass(value) {
    if (value === "high") return "risk-high";
    if (value === "medium") return "risk-medium";
    return "risk-low";
}

async function refreshMetrics() {
    const metrics = await api("/api/metrics");

    el.readinessScore.textContent = metrics.gatewayReadinessScore ?? "--";
    el.rating.textContent = metrics.rating || "waiting";
    el.rating.className = `score-rating ${ratingClass(metrics.rating)}`;
    el.summary.textContent = metrics.executiveSummary || "Run a simulation to create gateway events.";

    el.totalRequests.textContent = metrics.totalRequests || 0;
    el.allowedRequests.textContent = metrics.allowedRequests || 0;
    el.blockedRequests.textContent = metrics.blockedRequests || 0;
    el.openTickets.textContent = metrics.openTickets || 0;
}

async function refreshEvents() {
    const events = await api("/api/events");

    el.eventSubtitle.textContent = `${events.length} gateway events currently in memory.`;

    if (!events.length) {
        el.eventList.innerHTML = `
            <div class="empty-state">
                <span>🛡️</span>
                <p>GateWatch is standing by.</p>
            </div>
        `;
        return;
    }

    el.eventList.innerHTML = events.slice(0, 20).map(event => `
        <article class="event-card ${event.decision === "blocked" ? "blocked" : "allowed"}">
            <div class="event-top">
                <div>
                    <span class="event-path">${escapeHtml(event.method)} ${escapeHtml(event.path)}</span>
                    <small>${new Date(event.timestamp).toLocaleString()} • ${escapeHtml(event.sourceType)}</small>
                </div>
                <span class="pill ${event.decision === "blocked" ? severityClass(event.severity) : "risk-low"}">${escapeHtml(event.decision)}</span>
            </div>

            <div class="event-grid">
                <div>
                    <span>Rule</span>
                    <strong>${escapeHtml(event.ruleId)}</strong>
                </div>
                <div>
                    <span>Severity</span>
                    <strong>${escapeHtml(event.severity)}</strong>
                </div>
                <div>
                    <span>Source</span>
                    <strong>${escapeHtml(event.sourceIp)}</strong>
                </div>
                <div>
                    <span>Query</span>
                    <strong>${escapeHtml(event.query || "None")}</strong>
                </div>
            </div>

            <p>${escapeHtml(event.explanation)}</p>
            <small class="recommendation">${escapeHtml(event.recommendation)}</small>
        </article>
    `).join("");
}

async function loadRules() {
    const rules = await api("/api/rules");

    el.rulesList.innerHTML = rules.map(rule => `
        <article class="mini-card">
            <div>
                <span class="pill ${severityClass(rule.severity)}">${escapeHtml(rule.severity)}</span>
                <strong>${escapeHtml(rule.id)} • ${escapeHtml(rule.title)}</strong>
            </div>
            <p>${escapeHtml(rule.description)}</p>
            <small>${escapeHtml(rule.recommendation)}</small>
        </article>
    `).join("");
}

async function loadHeaders() {
    const headers = await api("/api/headers");

    el.headersList.innerHTML = headers.map(header => `
        <article class="mini-card">
            <div>
                <span class="pill risk-low">${escapeHtml(header.status)}</span>
                <strong>${escapeHtml(header.name)}</strong>
            </div>
            <p>${escapeHtml(header.purpose)}</p>
            <small>${escapeHtml(header.value)}</small>
        </article>
    `).join("");
}

async function loadTickets() {
    const tickets = await api("/api/tickets");

    if (!tickets.length) {
        el.ticketList.innerHTML = `<div class="ops-card"><p>No open tickets yet. Run a blocked simulation to create remediation work.</p></div>`;
        return;
    }

    el.ticketList.innerHTML = tickets.slice(0, 8).map(ticket => `
        <article class="ticket-card">
            <div>
                <span class="pill ${severityClass(ticket.severity)}">${escapeHtml(ticket.severity)}</span>
                <strong>${escapeHtml(ticket.id)} • ${escapeHtml(ticket.status)}</strong>
            </div>
            <p>${escapeHtml(ticket.title)}</p>
            <small>${escapeHtml(ticket.owner)} • ${escapeHtml(ticket.recommendation)}</small>
        </article>
    `).join("");
}

async function loadAiSummary() {
    const data = await api("/api/ai/summary");

    el.aiSummary.innerHTML = `
        <p>${escapeHtml(data.summary)}</p>
        <div class="tag-row">
            ${(data.recommendedNextActions || []).map(item => `<span>${escapeHtml(item)}</span>`).join("")}
        </div>
    `;
}

async function loadTrends() {
    const trends = await api("/api/trends");

    el.trendBars.innerHTML = trends.map(point => {
        const allowed = Number(point.allowed || 0);
        const blocked = Number(point.blocked || 0);
        const total = Math.max(1, allowed + blocked);
        const blockedHeight = Math.max(6, blocked * 18);
        const allowedHeight = Math.max(6, allowed * 18);

        return `
            <div class="trend-point">
                <div class="trend-stack">
                    <span class="bar blocked-bar" style="height:${blockedHeight}px"></span>
                    <span class="bar allowed-bar" style="height:${allowedHeight}px"></span>
                </div>
                <small>${escapeHtml(point.label)}</small>
                <strong>${total - 1 >= 0 ? allowed + blocked : 0}</strong>
            </div>
        `;
    }).join("");
}

async function loadSiemExport() {
    const data = await api("/api/siem/export");

    el.siemExport.innerHTML = `
        <div class="ops-metric">
            <span>Export Format</span>
            <strong>${escapeHtml(data.format)}</strong>
        </div>
        <div class="ops-metric">
            <span>Records Ready</span>
            <strong>${data.recordCount || 0}</strong>
        </div>
        <a href="/api/siem/export" target="_blank" class="inline-link">Open SIEM JSON</a>
    `;
}

async function loadCloudReadiness() {
    const data = await api("/api/cloud/readiness");

    el.cloudReadiness.innerHTML = `
        <div class="ops-metric">
            <span>Health Check</span>
            <strong>${escapeHtml(data.healthCheckPath)}</strong>
        </div>
        <div class="ops-metric">
            <span>Container Port</span>
            <strong>${escapeHtml(data.containerPort)}</strong>
        </div>
        <p>${escapeHtml(data.loadBalancerMode)}</p>
        <div class="tag-row">
            ${(data.networkControls || []).map(item => `<span>${escapeHtml(item)}</span>`).join("")}
        </div>
    `;
}

async function loadWafComparison() {
    const data = await api("/api/cloud/waf-comparison");

    el.wafComparison.innerHTML = `
        <p>${escapeHtml(data.recommendation)}</p>
        <div class="ops-metric">
            <span>GateWatch Rules</span>
            <strong>${(data.gatewatchRules || []).length}</strong>
        </div>
        <div class="tag-row">
            ${(data.awsWafMapping || []).map(item => `<span>${escapeHtml(item)}</span>`).join("")}
        </div>
    `;
}

async function loadKubernetesStatus() {
    const data = await api("/api/cloud/kubernetes-manifests");

    el.kubernetesStatus.innerHTML = `
        <div class="ops-metric">
            <span>Deployment</span>
            <strong>${escapeHtml(data.deployment)}</strong>
        </div>
        <div class="ops-metric">
            <span>Service</span>
            <strong>${escapeHtml(data.service)}</strong>
        </div>
        <div class="ops-metric">
            <span>Ingress</span>
            <strong>${escapeHtml(data.ingress)}</strong>
        </div>
        <p>${escapeHtml(data.note)}</p>
    `;
}

async function runScenario(scenario) {
    await api(`/api/simulate/${scenario}`, { method: "POST", body: "{}" });
    await refreshAll();
}

async function refreshAll() {
    await refreshMetrics();
    await refreshEvents();
    await loadTickets();
    await loadAiSummary();
    await loadTrends();
    await loadSiemExport();
    await loadCloudReadiness();
    await loadWafComparison();
    await loadKubernetesStatus();
}

function escapeHtml(value) {
    return String(value ?? "")
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#039;");
}

document.querySelectorAll("[data-scenario]").forEach(button => {
    button.addEventListener("click", () => runScenario(button.dataset.scenario));
});

document.getElementById("simulateAllowed").addEventListener("click", () => runScenario("allowed"));

document.getElementById("clearEvents").addEventListener("click", async () => {
    await api("/api/events/clear", { method: "POST", body: "{}" });
    await refreshAll();
});

document.getElementById("refreshBtn").addEventListener("click", refreshAll);

refreshAll().catch(console.error);
loadRules().catch(console.error);
loadHeaders().catch(console.error);
