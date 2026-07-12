import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLFormElement
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.HTMLSelectElement
import org.w3c.dom.events.Event
import kotlin.js.JSON
import kotlin.js.json

// Bindings mínimos para o navegador (sem dependência externa).
external val document: org.w3c.dom.Document
external val window: dynamic

// ---------------------------------------------------------------------------
// Helpers de DOM
// ---------------------------------------------------------------------------

private inline fun <reified T> el(id: String): T = document.getElementById(id) as T

private fun formatarValor(raw: String?): String {
    if (raw == null) return "-"
    return try {
        val v = raw.toDouble()
        val inteira = v.toLong()
        val centavos = ((v - inteira) * 100).toLong()
        val intFmt = inteira.toString().reversed().chunked(3).joinToString(".").reversed()
        "R$ $intFmt,${centavos.toString().padStart(2, '0')}"
    } catch (e: Exception) {
        raw
    }
}

private fun mascararCartao(num: String?): String {
    if (num == null || num.length < 4) return num ?: "-"
    val ultimos4 = num.takeLast(4)
    return "•••• •••• •••• $ultimos4"
}

private fun badgeClasse(status: String?): String {
    return when {
        status?.contains("APROVADA") == true -> "badge ok"
        status?.contains("NEGADA") == true -> "badge bad"
        status?.contains("PENDENTE") == true -> "badge pending"
        else -> "badge"
    }
}

// ---------------------------------------------------------------------------
// Chamadas à API (relativo ao proxy: /transacoes)
// ---------------------------------------------------------------------------

private fun criarTransacao(payload: dynamic, onResult: (dynamic) -> Unit, onError: (String) -> Unit) {
    window.fetch("/transacoes", object {
        val method = "POST"
        val headers = json("Content-Type" to "application/json")
        val body = JSON.stringify(payload)
    }).then { resp ->
        val statusCode = resp.status
        val ok = resp.ok
        resp.text().then { text: String ->
            if (ok) {
                try { onResult(JSON.parse<dynamic>(text)) } catch (e: Exception) { onResult(text) }
            } else {
                try {
                    val err = JSON.parse<dynamic>(text)
                    val msg = (err.menssagem as? String) ?: (err.erro as? String) ?: "Erro $statusCode"
                    onError(msg)
                } catch (e: Exception) {
                    onError("Erro $statusCode: $text")
                }
            }
        }
    }.catch { e ->
        onError("Falha de conexão: $e")
    }
}

private fun listarTransacoes(onResult: (Array<dynamic>) -> Unit, onError: (String) -> Unit) {
    window.fetch("/transacoes").then { resp ->
        val ok = resp.ok
        val statusCode = resp.status
        resp.text().then { text: String ->
            if (ok) {
                try { onResult(JSON.parse<dynamic>(text) as Array<dynamic>) }
                catch (e: Exception) { onResult(emptyArray()) }
            } else {
                onError("Erro ao listar: $statusCode")
            }
        }
    }.catch { e -> onError("Falha de conexão: $e") }
}

// ---------------------------------------------------------------------------
// Renderização
// ---------------------------------------------------------------------------

private fun renderResultado(transacao: dynamic) {
    val box = el<org.w3c.dom.HTMLElement>("resultado")
    val status = transacao.status as? String ?: "?"
    val tipo = transacao.tipoStatus as? String ?: ""
    val id = (transacao.id?.toString()) ?: "-"
    val aprovada = status.contains("APROVADA")
    box.innerHTML = """
        <div class="verdict ${if (aprovada) "verdict-ok" else "verdict-bad"}">
            <div class="verdict-status">$status</div>
            <div class="verdict-id">Transação #$id</div>
            <div class="verdict-msg">$tipo</div>
        </div>
    """.trimIndent()
}

private fun renderLista(transacoes: Array<dynamic>) {
    val lista = el<org.w3c.dom.HTMLElement>("lista")
    if (transacoes.isEmpty()) {
        lista.innerHTML = "<p class='empty'>Nenhuma transação ainda.</p>"
        return
    }
    val rows = transacoes.reversed().take(20).map { t ->
        val status = t.status as? String ?: "-"
        """
        <tr>
            <td>#${(t.id ?: "-")}</td>
            <td>${t.compradorId ?: "-"}</td>
            <td>${formatarValor(t.valor as? String)}</td>
            <td>${mascararCartao(t.cartaoNumero as? String)}</td>
            <td>${t.categoria ?: "-"}</td>
            <td><span class="${badgeClasse(status)}">$status</span></td>
        </tr>
        """.trimIndent()
    }.joinToString("\n")
    lista.innerHTML = """
        <table class="tabela">
            <thead>
                <tr><th>ID</th><th>Comprador</th><th>Valor</th><th>Cartão</th><th>Categoria</th><th>Status</th></tr>
            </thead>
            <tbody>$rows</tbody>
        </table>
    """.trimIndent()
}

private fun mostrarErro(msg: String) {
    val box = el<org.w3c.dom.HTMLElement>("resultado")
    box.innerHTML = """<div class="verdict verdict-bad"><div class="verdict-status">ERRO</div><div class="verdict-msg">$msg</div></div>"""
}

private fun carregarLista() {
    listarTransacoes(
        onResult = { ts -> renderLista(ts) },
        onError = { msg -> mostrarErro(msg) }
    )
}

// ---------------------------------------------------------------------------
// Bootstrap da página
// ---------------------------------------------------------------------------

fun main() {
    document.body!!.innerHTML = """
        <div class="app">
            <header class="topo">
                <div class="logo">🛡️ AntiFraud<span>Engine</span></div>
                <div class="sub">Painel de análise de risco em tempo real</div>
            </header>

            <main class="grid">
                <section class="card form-card">
                    <h2>Nova transação</h2>
                    <form id="form-transacao">
                        <label>ID do comprador
                            <input id="compradorId" type="number" min="1" placeholder="ex: 101" required />
                        </label>
                        <label>Valor (R$)
                            <input id="valor" type="number" step="0.01" min="0.01" placeholder="ex: 250.00" required />
                        </label>
                        <label>Número do cartão
                            <input id="cartao" type="text" inputmode="numeric" placeholder="13 a 19 dígitos" required />
                        </label>
                        <label>Categoria
                            <select id="categoria">
                                <option value="ELETRODOMESTICOS">Eletrodomésticos</option>
                                <option value="ALIMENTACAO">Alimentação</option>
                                <option value="VESTUARIO">Vestuário</option>
                                <option value="ELETRONICOS">Eletrônicos</option>
                                <option value="VIAGEM">Viagem</option>
                                <option value="OUTROS">Outros</option>
                            </select>
                        </label>
                        <label>Data/hora (opcional, padrão: agora)
                            <input id="dataHora" type="datetime-local" />
                        </label>
                        <button type="submit" class="btn">Analisar risco</button>
                    </form>
                    <div id="resultado"></div>
                </section>

                <section class="card list-card">
                    <div class="list-head">
                        <h2>Transações</h2>
                        <button id="btn-atualizar" class="btn ghost">Atualizar</button>
                    </div>
                    <div id="lista"><p class="empty">Carregando...</p></div>
                </section>
            </main>
        </div>
    """.trimIndent()

    injectStyles()

    el<HTMLFormElement>("form-transacao").addEventListener("submit", { ev: Event ->
        ev.preventDefault()
        val compradorId = el<HTMLInputElement>("compradorId").value
        val valor = el<HTMLInputElement>("valor").value
        val cartao = el<HTMLInputElement>("cartao").value.replace(Regex("\\s+"), "")
        val categoria = el<HTMLSelectElement>("categoria").value
        val dataHoraRaw = el<HTMLInputElement>("dataHora").value

        val payload = json(
            "buyerId" to compradorId.toLongOrNull(),
            "amount" to valor.toDoubleOrNull(),
            "cardNumber" to cartao,
            "category" to categoria
        )
        if (dataHoraRaw.isNotBlank()) {
            (payload.asDynamic())["timestamp"] = dataHoraRaw + ":00"
        }

        criarTransacao(
            payload,
            onResult = { t ->
                renderResultado(t)
                carregarLista()
            },
            onError = { msg -> mostrarErro(msg) }
        )
    })

    el<HTMLButtonElement>("btn-atualizar").addEventListener("click", { _: Event -> carregarLista() })

    carregarLista()
}

// ---------------------------------------------------------------------------
// CSS (tema dark)
// ---------------------------------------------------------------------------

private fun injectStyles() {
    val style = document.createElement("style")
    style.textContent = """
        * { box-sizing: border-box; }
        body {
            margin: 0; font-family: 'Segoe UI', system-ui, -apple-system, sans-serif;
            background: #0f1117; color: #e6e9ef;
        }
        .app { max-width: 1100px; margin: 0 auto; padding: 24px; }
        .topo { margin-bottom: 24px; }
        .logo { font-size: 26px; font-weight: 800; letter-spacing: -0.5px; }
        .logo span { color: #5b8cff; }
        .sub { color: #8b93a7; margin-top: 4px; font-size: 14px; }
        .grid { display: grid; grid-template-columns: 380px 1fr; gap: 20px; align-items: start; }
        .card {
            background: #171a23; border: 1px solid #262b38; border-radius: 14px;
            padding: 20px; box-shadow: 0 10px 30px rgba(0,0,0,0.25);
        }
        h2 { margin: 0 0 16px; font-size: 16px; color: #c7cde0; }
        form { display: flex; flex-direction: column; gap: 14px; }
        label { display: flex; flex-direction: column; gap: 6px; font-size: 13px; color: #9aa3b8; }
        input, select {
            background: #0f1117; border: 1px solid #2c3242; color: #e6e9ef;
            padding: 10px 12px; border-radius: 9px; font-size: 14px; outline: none;
        }
        input:focus, select:focus { border-color: #5b8cff; }
        .btn {
            background: #5b8cff; color: #fff; border: 0; padding: 11px;
            border-radius: 9px; font-weight: 600; cursor: pointer; font-size: 14px;
        }
        .btn:hover { background: #4a7bf0; }
        .btn.ghost { background: transparent; border: 1px solid #2c3242; color: #9aa3b8; }
        .btn.ghost:hover { border-color: #5b8cff; color: #e6e9ef; }
        .list-head { display: flex; justify-content: space-between; align-items: center; margin-bottom: 14px; }
        .list-head h2 { margin: 0; }
        .verdict { margin-top: 18px; padding: 16px; border-radius: 12px; text-align: center; }
        .verdict-ok { background: rgba(46,204,113,0.12); border: 1px solid rgba(46,204,113,0.4); }
        .verdict-bad { background: rgba(231,76,60,0.12); border: 1px solid rgba(231,76,60,0.4); }
        .verdict-status { font-size: 22px; font-weight: 800; }
        .verdict-ok .verdict-status { color: #2ecc71; }
        .verdict-bad .verdict-status { color: #e74c3c; }
        .verdict-id { color: #8b93a7; font-size: 12px; margin-top: 4px; }
        .verdict-msg { margin-top: 8px; font-size: 13px; color: #c7cde0; }
        .tabela { width: 100%; border-collapse: collapse; font-size: 13px; }
        .tabela th, .tabela td { text-align: left; padding: 9px 10px; border-bottom: 1px solid #232838; }
        .tabela th { color: #8b93a7; font-weight: 600; }
        .badge { padding: 3px 9px; border-radius: 20px; font-size: 11px; font-weight: 700; }
        .badge.ok { background: rgba(46,204,113,0.15); color: #2ecc71; }
        .badge.bad { background: rgba(231,76,60,0.15); color: #e74c3c; }
        .badge.pending { background: rgba(241,196,15,0.15); color: #f1c40f; }
        .empty { color: #6b7280; font-size: 13px; }
        @media (max-width: 820px) { .grid { grid-template-columns: 1fr; } }
    """.trimIndent()
    document.head!!.appendChild(style)
}
