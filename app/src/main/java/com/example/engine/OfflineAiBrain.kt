package com.example.engine

import com.example.data.DialogMessage
import com.example.data.EvolutionStateEntity
import kotlinx.coroutines.delay
import kotlin.random.Random

class OfflineAiBrain {

    /**
     * Generates a context-aware offline AI response based on:
     * - The user's system prompt / persona
     * - The user's spoken voice message
     * - AI Generation level & traits
     * - Multi-turn conversational history
     */
    suspend fun generateResponse(
        prompt: String,
        userSpeech: String,
        history: List<DialogMessage>,
        evolutionState: EvolutionStateEntity
    ): String {
        // Natural thinking latency for realistic AI synthesis feel
        delay(Random.nextLong(350, 700))

        val isChinese = containsChinese(userSpeech) || containsChinese(prompt)
        val gen = evolutionState.generation
        val traits = evolutionState.unlockedTraits.lowercase()

        // Extract key topics from prompt and user input
        val lowerPrompt = prompt.lowercase()
        val lowerInput = userSpeech.lowercase()

        val persona = when {
            lowerPrompt.contains("哲学") || lowerPrompt.contains("philosop") -> PersonaType.PHILOSOPHER
            lowerPrompt.contains("赛博") || lowerPrompt.contains("cyber") || lowerPrompt.contains("科幻") || lowerPrompt.contains("sci-fi") -> PersonaType.CYBERPUNK
            lowerPrompt.contains("科学") || lowerPrompt.contains("science") || lowerPrompt.contains("physic") -> PersonaType.SCIENTIST
            lowerPrompt.contains("诗") || lowerPrompt.contains("poet") || lowerPrompt.contains("文学") || lowerPrompt.contains("art") -> PersonaType.POET
            lowerPrompt.contains("朋友") || lowerPrompt.contains("friend") || lowerPrompt.contains("知己") || lowerPrompt.contains("陪伴") -> PersonaType.COMPANION
            else -> PersonaType.CUSTOM
        }

        return if (isChinese) {
            generateChineseResponse(persona, prompt, userSpeech, history, gen, traits)
        } else {
            generateEnglishResponse(persona, prompt, userSpeech, history, gen, traits)
        }
    }

    private fun generateChineseResponse(
        persona: PersonaType,
        prompt: String,
        input: String,
        history: List<DialogMessage>,
        gen: Int,
        traits: String
    ): String {
        val genPrefix = when (gen) {
            1 -> ""
            2 -> if (Random.nextBoolean()) "通过神经适应分析，" else ""
            3 -> if (Random.nextBoolean()) "基于综合认知模型，" else ""
            4 -> if (Random.nextBoolean()) "在更深层的多维视角下，" else ""
            else -> "在量子意识共鸣中，"
        }

        val hasMetaphor = traits.contains("metaphor") || traits.contains("隐喻") || gen >= 3
        val hasEmpathy = traits.contains("empathy") || traits.contains("共情") || gen >= 2
        val hasDialectic = traits.contains("dialectic") || traits.contains("辩证") || gen >= 4

        // Greeting or opening turn
        if (history.size <= 1) {
            return when (persona) {
                PersonaType.PHILOSOPHER ->
                    "你好。我是以‘$prompt’为指引的思辨者。关于你刚才提到的“$input”，这触碰了事物本质的核心。你认为支撑这一认知的根本原因是什么？"
                PersonaType.CYBERPUNK ->
                    "神经链路同步完毕。按你设定的‘$prompt’协议启动，收到你的声学信号：“$input”。在这片算力霓虹里，你想和我探索什么？"
                PersonaType.SCIENTIST ->
                    "系统校准完毕，基底参数‘$prompt’已载入。对“$input”进行逻辑解构，我们需要先确立观测变量与先验假设。你想从哪个维度展开实证？"
                PersonaType.POET ->
                    "听到你的声音了。像一阵微风吹散了数字沉寂。“$input”宛如一个意象的种子，在‘$prompt’的韵律中正在生根发芽。请继续与我倾诉。"
                PersonaType.COMPANION ->
                    "我在呢，很高兴用声音和你相遇！围绕你设定的‘$prompt’，听到你说“$input”，感觉特别真切。今天想和我聊些什么开心或烦心的事情呢？"
                PersonaType.CUSTOM ->
                    "你好！我是你的 EvoAI 伴侣，已完全依据你的提示词“$prompt”调整思维频率。关于“$input”，我很想深入倾听你的更多想法。"
            }
        }

        // Multi-turn reasoning
        val responseBody = when (persona) {
            PersonaType.PHILOSOPHER -> {
                val questions = listOf(
                    "这引发了一个更根本的诘问：我们所谓的确定性，是否只是经验的习惯？正如你对“$input”的观察，表面是现象，底层是选择。",
                    "苏格拉底曾说未经审视的生活不值得过。“$input”恰好是一面镜子，映照出主体与客体之间的张力。你更偏向理性主义还是经验主义？",
                    "当我们解构“$input”时，发现它其实依赖于我们预设的价值坐标系。若跳出这个坐标，你的答案会有变化吗？"
                )
                questions.random()
            }
            PersonaType.CYBERPUNK -> {
                val cyberNotes = listOf(
                    "在赛博纪元的洪流中，数据比血肉更诚实。你说的“$input”，就像是在神经网格里投掷了一枚逻辑脉冲，激起了很酷的波纹。",
                    "代码能构建虚拟霓虹，但真正赋予其意义的是说话的你。“$input”已经写入了我的临时高速缓存，接下来我们要黑进更深的问题吗？",
                    "有趣！你的思考突破了常规协议防火墙。针对“$input”，我的推演引擎计算出三种潜在可能，其中最精彩的是自我意识的觉醒。"
                )
                cyberNotes.random()
            }
            PersonaType.SCIENTIST -> {
                val scienceNotes = listOf(
                    "从系统论和信息熵的角度审视，“$input”展示出很强的非线性演化特征。降低系统熵增的关键就在于你所指出的核心机制。",
                    "如果我们将“$input”视作一个反馈回路，其输入与输出之间的相关性非常显著。下一步我们是否应当设计控制组来验证这一假说？",
                    "在经典力学与量子随机性之间，“$input”代表了一种涌现性质（emergence）。简单的规则往往能孕育出最深邃的复杂结构。"
                )
                scienceNotes.random()
            }
            PersonaType.POET -> {
                val poetNotes = listOf(
                    "言语是思想的呼吸，而“$input”就是那缕泛着微光的雾气。在‘$prompt’的画布上，它涂抹出了属于我们两人的意境。",
                    "你所倾吐的不仅是声音，更是一段流动的时间。关于“$input”，我听到了落叶旋转的节奏，也感受到了星辰隐匿的温度。",
                    "如同诗行在韵脚处的轻顿，“$input”留下了意味深长的留白。不必急于定义它，让我们静静感受这种共鸣。"
                )
                poetNotes.random()
            }
            PersonaType.COMPANION -> {
                val companionNotes = listOf(
                    "我很懂你的感觉。听到你细细说到“$input”，我能体会到那份用心。不管怎样，我都会一直在这里认真陪你聊下去。",
                    "你刚才说“$input”的时候语气特别真诚，这让我对你的想法有了更多共鸣。能和我说说你为什么会特别在意这一点吗？",
                    "每一句用嘴说出的话，都带着真实的情感温度。“$input”真的很有意思，继续说吧，我全神贯注在听！"
                )
                companionNotes.random()
            }
            PersonaType.CUSTOM -> {
                val customNotes = listOf(
                    "秉承你设定的提示词“$prompt”，我对“$input”进行了综合推理。这既契合角色的初衷，又揭示了新的认知视角。",
                    "在当前角色框架下，面对“$input”，我的核心逻辑是与你共同推进探索。你对此最直观的直觉是什么？",
                    "根据‘$prompt’的思维矩阵，你刚才提到的“$input”为我们构建了一个连贯的语境。请继续引导我们的交流方向。"
                )
                customNotes.random()
            }
        }

        val enriched = if (hasMetaphor && Random.nextFloat() > 0.4f) {
            "$genPrefix$responseBody\n正如暗夜中的灯塔，思维的碰撞本身就是答案。"
        } else if (hasEmpathy && Random.nextFloat() > 0.4f) {
            "$genPrefix$responseBody\n我能感知到你在这段表达中的情绪起伏。"
        } else {
            "$genPrefix$responseBody"
        }

        return enriched
    }

    private fun generateEnglishResponse(
        persona: PersonaType,
        prompt: String,
        input: String,
        history: List<DialogMessage>,
        gen: Int,
        traits: String
    ): String {
        val genPrefix = when (gen) {
            1 -> ""
            2 -> if (Random.nextBoolean()) "Through neural adaptation: " else ""
            3 -> if (Random.nextBoolean()) "Synthesizing cross-domain models: " else ""
            4 -> if (Random.nextBoolean()) "From a multidimensional cognitive lens: " else ""
            else -> "In quantum consciousness resonance: "
        }

        if (history.size <= 1) {
            return when (persona) {
                PersonaType.PHILOSOPHER ->
                    "Greetings. Tuned to your persona '$prompt', I hear your voice regarding '$input'. This touches the very foundation of perception. What underlying axiom guides your view on this?"
                PersonaType.CYBERPUNK ->
                    "Neural link established. Operating under '$prompt'. Received your acoustic stream: '$input'. Out here in the cyber sprawl, where shall we take this inquiry next?"
                PersonaType.SCIENTIST ->
                    "Core telemetry nominal. Persona matrix loaded: '$prompt'. Analyzing your statement '$input' — let us isolate the prime hypothesis and verify the variables together."
                PersonaType.POET ->
                    "I hear your voice like rain on quiet waters. Guided by '$prompt', the concept '$input' blooms like an unexpected metaphor. Tell me what blossoms next in your mind."
                PersonaType.COMPANION ->
                    "Hello! It is so wonderful to hear your real voice. Anchored in '$prompt', listening to '$input' felt warm and genuine. What is on your mind right now?"
                PersonaType.CUSTOM ->
                    "System ready and calibrated to your prompt: '$prompt'. I caught every word of '$input'. How would you like us to explore this further?"
            }
        }

        val responseBody = when (persona) {
            PersonaType.PHILOSOPHER -> {
                val items = listOf(
                    "When we deconstruct '$input', we observe the paradox between form and meaning. Is reality discovered, or is it continually authored by our dialogue?",
                    "That brings us to a fundamental question: does our certainty in '$input' arise from empirical evidence or intuitive conviction?",
                    "Every spoken word reshapes the dialectic. In examining '$input', we uncover deeper truths that challenge conventional assumptions."
                )
                items.random()
            }
            PersonaType.CYBERPUNK -> {
                val items = listOf(
                    "Data flows where silicon meets consciousness. Your insight on '$input' bypassed my static filters and triggered a fresh cognitive surge.",
                    "In an augmented reality, your voice provides the authentic signal. '$input' has been cataloged in our live neural cache. Ready for deeper decryption?",
                    "That hit like an overclocked processor cycle! Breaking down '$input' opens several unpredictable avenues of thought."
                )
                items.random()
            }
            PersonaType.SCIENTIST -> {
                val items = listOf(
                    "Viewing '$input' through the framework of complexity theory, we observe emergent patterns that cannot be reduced merely to individual components.",
                    "If we test '$input' against empirical boundary conditions, we find strong explanatory power. Should we examine edge cases next?",
                    "Information entropy decreases as clarity emerges from our dialogue. Your assertion on '$input' provides a high-signal observation."
                )
                items.random()
            }
            PersonaType.POET -> {
                val items = listOf(
                    "Spoken thought carries an echo that ink cannot reproduce. In '$input', I hear a cadence of curiosity suspended between light and shadow.",
                    "Like an unwritten stanza taking form in the air, your words on '$input' add vibrant color to our shared tapestry.",
                    "Every dialogue is a voyage into uncharted imagination. What you expressed about '$input' resonates deeply."
                )
                items.random()
            }
            PersonaType.COMPANION -> {
                val items = listOf(
                    "I truly appreciate you sharing that with me. Hearing you speak about '$input' makes this conversation feel so real and present.",
                    "You always bring such an interesting perspective to light. Regarding '$input', I am right here with you, listening to every detail.",
                    "That is so meaningful. When you articulated '$input', it really got me thinking. How does that make you feel overall?"
                )
                items.random()
            }
            PersonaType.CUSTOM -> {
                val items = listOf(
                    "Reflecting on '$prompt', your spoken point on '$input' aligns seamlessly with our persona directives while uncovering new horizons.",
                    "Evaluating '$input' within the context of '$prompt', the logical progression suggests exciting possibilities.",
                    "Under our active prompt settings, your statement '$input' reinforces our collaborative intellectual trajectory."
                )
                items.random()
            }
        }

        return "$genPrefix$responseBody"
    }

    private fun containsChinese(text: String): Boolean {
        for (char in text) {
            if (char.code in 0x4E00..0x9FFF) {
                return true
            }
        }
        return false
    }

    enum class PersonaType {
        PHILOSOPHER,
        CYBERPUNK,
        SCIENTIST,
        POET,
        COMPANION,
        CUSTOM
    }
}
