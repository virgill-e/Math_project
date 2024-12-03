package boggle;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import tree.LexicographicTree;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultUndirectedGraph;

/**
 * class de representant un boggle
 *
 * @author virgi
 *
 */
public class Boggle {
    private final Graph<Sommet, DefaultEdge> graph;
    private LexicographicTree arbre;
    private static final String DICTIONARY = "src/main/resources/mots/dictionnaire_FR_sans_accents.txt";
    /*
     * CONSTRUCTORS
     */

    /**
     * Constructor : creates a Boggle grid filled with random letters.
     *
     * @param size The size of the squared grid
     * @param dict A dictionary of allowed words
     */
    public Boggle(int size, LexicographicTree dict) {
        if (size < 1)
            throw new IllegalArgumentException("Size must be greater than 0.");

        if (dict == null)
            throw new IllegalArgumentException("null pointer exception");

        this.graph = new DefaultUndirectedGraph<>(DefaultEdge.class);
        Sommet[][] grille = new Sommet[size][size];

        Random rand = new Random();
        for (int i = 0; i < size * size; i++) {
            int row = i / size;
            int col = i % size;
            char letter = (char) ('a' + rand.nextInt(26));
            grille[row][col] = new Sommet(letter);
        }
        setVertexGraph(grille);
        setEdgeGraph(grille);

    }

    /**
     * Constructor : creates a Boggle grid filled with the supplied letters.
     *
     * @param size    The size of the squared grid
     * @param letters A string containing the (size x size) letters used to fill the
     *                grid
     * @param dict    A dictionary of allowed words
     */
    public Boggle(int size, String letters, LexicographicTree dict) {
        if (letters == null) {
            throw new IllegalArgumentException("Invalid grid size or letters");
        }
        if (size < 1 || letters.length() < size * size) {
            throw new IllegalArgumentException("Invalid grid size or letters");
        }
        this.graph = new DefaultUndirectedGraph<>(DefaultEdge.class);
        arbre = dict;

        Sommet[][] grille = new Sommet[size][size];

        for (int i = 0; i < size * size; i++) {
            int row = i / size;
            int col = i % size;
            grille[row][col] = new Sommet(letters.charAt(i));
            ;
        }
        setVertexGraph(grille);
        setEdgeGraph(grille);

    }

    /*
     * PUBLIC METHODS
     */

    /**
     * Returns the letters in the Boggle grid.
     *
     * @return a string of letters
     */
    public String letters() {
        StringBuilder sb = new StringBuilder();
        Set<Sommet> vertexSet = graph.vertexSet();
        for (Sommet sommet : vertexSet) {
            sb.append(sommet.getLetter());
        }
        return sb.toString();
    }

    /**
     * Determines if a word can be found in the Boggle grid.
     *
     * @param givenWord a word
     * @return true if the word is present, false otherwise
     */
    public boolean contains(String givenWord) {
        if (givenWord == null)
            return false;
        String word = givenWord.toLowerCase();
        if (word == null || word.isEmpty()) {
            return false;
        }

        if (!arbre.containsWord(word)) {
            return false;
        }

        for (Sommet sommet : graph.vertexSet()) {
            if (sommet.getLetter() != word.charAt(0))
                continue;
            if (searchWord(sommet, word.substring(1))) {
                return true;
            }
        }

        return false;
    }

    /**
     * Searches for words in the Boggle grid.
     *
     * @return the set of found words
     */
    public Set<String> solve() {
        Set<String> motsTrouves = new HashSet<>();
        Set<Sommet> vertexSet = graph.vertexSet();
        for (Sommet sommet : vertexSet) {
            dfs(sommet, "" + sommet.getLetter(), motsTrouves);
        }
        return motsTrouves;
    }

    /**
     * Returns a textual representation of the Boggle grid.
     *
     * @return a textual representation of the Boggle grid
     */
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        Set<Sommet> vertexSet = graph.vertexSet();

        int gridSize = (int) Math.sqrt(vertexSet.size());
        int nbChar = 0;
        for (Sommet sommet : vertexSet) {
            stringBuilder.append(sommet.getLetter());
            nbChar++;
            if (nbChar % gridSize == 0) {
                stringBuilder.append("\n");
            } else
                stringBuilder.append(' ');
        }

        return stringBuilder.toString();
    }

    /*
     * PRIVATE METHODS
     */

    private void setVertexGraph(Sommet[][] lettersTab) {
        for (Sommet[] tab : lettersTab) {
            for (Sommet sommet : tab) {
                graph.addVertex(sommet);
            }
        }
    }

    private void setEdgeGraph(Sommet[][] lettersTab) {
        int numRows = lettersTab.length;
        int numCols = lettersTab[0].length;

        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                setEdgeNeighbor(lettersTab, numRows, numCols, i, j);
            }
        }
    }

    private void setEdgeNeighbor(Sommet[][] lettersTab, int numRows, int numCols, int posI, int posJ) {
        Sommet current = lettersTab[posI][posJ];

        if (posI > 0 && posJ > 0) {
            graph.addEdge(current, lettersTab[posI - 1][posJ - 1]); // voisin en haut à gauche
        }
        if (posI > 0) {
            graph.addEdge(current, lettersTab[posI - 1][posJ]); // voisin en haut
        }
        if (posI > 0 && posJ < numCols - 1) {
            graph.addEdge(current, lettersTab[posI - 1][posJ + 1]); // voisin en haut à droite
        }
        if (posJ > 0) {
            graph.addEdge(current, lettersTab[posI][posJ - 1]); // voisin à gauche
        }
        if (posJ < numCols - 1) {
            graph.addEdge(current, lettersTab[posI][posJ + 1]); // voisin à droite
        }
        if (posI < numRows - 1 && posJ > 0) {
            graph.addEdge(current, lettersTab[posI + 1][posJ - 1]); // voisin en bas à gauche
        }
        if (posI < numRows - 1) {
            graph.addEdge(current, lettersTab[posI + 1][posJ]); // voisin en bas
        }
        if (posI < numRows - 1 && posJ < numCols - 1) {
            graph.addEdge(current, lettersTab[posI + 1][posJ + 1]); // voisin en bas à droite
        }
    }

    private void dfs(Sommet sommetCourant, String motCourant, Set<String> motsTrouves) {
        sommetCourant.setVisited(true);
        if (arbre.containsWord(motCourant) && motCourant.length() >= 3) {
            motsTrouves.add(motCourant);
        }
        if (arbre.isPrefix(motCourant)) {
            Set<DefaultEdge> edges = graph.edgesOf(sommetCourant);
            for (DefaultEdge edge : edges) {
                Sommet sommetVoisin = graph.getEdgeTarget(edge);
                if (sommetVoisin.equals(sommetCourant)) {
                    sommetVoisin = graph.getEdgeSource(edge);
                }
                if (!sommetVoisin.isVisited()) {
                    dfs(sommetVoisin, motCourant + sommetVoisin.getLetter(), motsTrouves);
                }
            }
        }
        sommetCourant.setVisited(false);
    }

    private boolean searchWord(Sommet sommetCourant, String mot) {
        if (mot.isEmpty()) {
            return true;
        }
        sommetCourant.setVisited(true);
        Set<DefaultEdge> edges = graph.edgesOf(sommetCourant);
        for (DefaultEdge edge : edges) {
            Sommet sommetVoisin = graph.getEdgeTarget(edge);
            if (sommetVoisin.equals(sommetCourant)) {
                sommetVoisin = graph.getEdgeSource(edge);
            }
            if (!sommetVoisin.isVisited() && sommetVoisin.getLetter() == mot.charAt(0)) {
                if (searchWord(sommetVoisin, mot.substring(1))) {
                    sommetCourant.setVisited(false);
                    return true;
                }
            }
        }

        sommetCourant.setVisited(false);
        return false;
    }

    /*
     * MAIN PROGRAM
     */

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        System.out.println("Loading dictionary...");
        LexicographicTree dictionary = new LexicographicTree(DICTIONARY);
        long loadDictTime = System.currentTimeMillis();
        System.out.println("Duration : " + (loadDictTime - startTime) / 1000.0);
        System.out.println("Number of words : " + dictionary.size());
        System.out.println();

        // Sample grids
        // String grid4x4 = "rhreypcswnsntego"; // Wikipedia example
        // Boggle boggle = new Boggle(4, grid4x4, dictionary);

//		String grid10x10 = "eymmccsrltjttsdiraoarliuniepeousrcgoiseerreeistiedtomcteevcmkaualilaretneerectresieenspgizeoeceecuds";
//		Boggle boggle = new Boggle(10, grid10x10, dictionary);

        // tring grid20x20 =
        // "eymmccsrltjttsdiraoarliuniepeousrcgoiseerreeistiedtomcteevcmkaualilaretneerectresieenspgizeoeceecudsrrsrvfianrsicwtdieioeiufnidlaaeeoeieitmntleavieacalischvzeatuisiupatolauaernetasatttadvtthzraaneuzfpneenabiielhcnitesaouelsenxrtojlcastieklkrupeletaiztleapqgaeocbpteutnetrtozatluuarapepsvipesxolteatmylttumelctahsowlsadoelouamisparejpmuaasoaeszsuilubrdrannyosfewnolneudpatcrwatblttpensaaunvkslrekiittc";
        // Boggle boggle = new Boggle(20, grid20x20, dictionary);

//		 String grid50x50 =
//		 "eymmccsrltjttsdiraoarliuniepeousrcgoiseerreeistiedtomcteevcmkaualilaretneerectresieenspgizeoeceecudsrrsrvfianrsicwtdieioeiufnidlaaeeoeieitmntleavieacalischvzeatuisiupatolauaernetasatttadvtthzraaneuzfpneenabiielhcnitesaouelsenxrtojlcastieklkrupeletaiztleapqgaeocbpteutnetrtozatluuarapepsvipesxolteatmylttumelctahsowlsadoelouamisparejpmuaasoaeszsuilubrdrannyosfewnolneudpatcrwatblttpensaaunvkslrekiittciivsomuestiurfuaxreeunuennetemubenanvsucimozentlvptnsoyaoatospesvaesasyysdlbdoraguhpleonvfrelentickiwzrnmimsaeimralovhetscejsdsnrtcsgporubtewesdklorlvteselauxieusieetfmiplllneuyprlpiiujiewverneussnnaxoaswclermderupyurmaareuescriqesbeeadldnlhtsnaucxeadstciqneeetcwtctcltavxgiiuorlomewbleeaoanrjeqeaqhzetmamisirasceranivleteeuaedeaatnsostwtbtonuasilsodhxsmnetecuoesepmotlndamvdcaeebiualneltdrtnwgerifterpepdetdbgollulneoynesonnrpesaustieundaevansmspaisinusitiaagrhoaeeewotnlagtlinjdssnocmeigvultkamnarvcloohslgiueawnyterddduepeislsmaemaiensuytiraesliehotcmaeoeovtsoiostialfertapbuptefeeleeonkeeectcdtneuidrlrpeenmeauvztltsetaeidlsrgscvlsenmetyeoueqesassooiajprrsytioqesugwvatixluutotimwlpesreeicylreeeseauueeeeapornntulivlonansipvoeeactiuecmeudnenrqaieordhluomrtsrmetetswlieqcmltslvsadeuspglmyruteoixiuoepdnectntentdaualdpcsoaeljvonkeftneiuedeeztsatencaectoeptluatriocdocrdtmudleueornptmeintlzejaaaneeradibraeaoaanpoisieeurtettrxvtneoegleltagkasosrastluadxsepnlsaadoaiepjswyedatmrsnivmriseaweinvatepciuuesssnllsssmixlesiedettssyoeuipwltetitececoieeozweaenmlaoroospptusidpkdvsrnqaajituspuuleiisheiogeinpbbsitbsvetofsncnaetaowooekmuntavroonjraduuacknoqqknnnrjoopeeofzdyseaoltsclvaaapiueceauofcdbntmxtneetpoitwiinfaeltgueeispzeacneqmviaiusaettplhiuaetqaewtfuuipoueuesnsoxaixaeeyavqllssqareessnmeolsetlvttbpbeoosesiuincpnersriiterrincnhsemaunvaseeueprldkiecnwtisultmmenensaojgidntetselyzagtctaisiraipzegeienjreosuuszuynlnpooesurddauuuitnouspiiuaeeqerelumdalnohdhueuuiiaiaaltlunnsnamleoprecysucviuirsatenctssjeniinreuenvirsntrwzntaeeieouapntlmayotrpsuunnuiptsxaevplkmuruasocuimontijceksmaeaaearurosaeitpimvtityevsualpsosallkkimiaaievplozjirncedcismssamnerotsprnltlhfiokolroleeaejexjslihseaoelqnsrwizluirhuraarefssdsealtkuediqtdpwekselinealineozeremtjandnrerracqoakiltrcsnwataavalommuslrdqawqpcneaiotajsaiedrkoxtasfyvermeyrnaibrdeiixlefsesvsqrlobkatcptiuxpmvanohcedlemkgevsuoexjjmenoteatptylewesoeotzbveiugseaswoeueoirpupdpulsidsiosueeealdepeltuwssipsecinicloeantylscemtsbairodutathtceeutmrsiarnptamasrrildiuwntaisaatculursrgeierrheeiteacuroruyfretvcxegadiiunguenunubreuflnccretdeetwmdunttrosyntooieeeutvenra";
//		 Boggle boggle = new Boggle(50, grid50x50, dictionary);

        // String grid100x100 =
        // "eymmccsrltjttsdiraoarliuniepeousrcgoiseerreeistiedtomcteevcmkaualilaretneerectresieenspgizeoeceecudsrrsrvfianrsicwtdieioeiufnidlaaeeoeieitmntleavieacalischvzeatuisiupatolauaernetasatttadvtthzraaneuzfpneenabiielhcnitesaouelsenxrtojlcastieklkrupeletaiztleapqgaeocbpteutnetrtozatluuarapepsvipesxolteatmylttumelctahsowlsadoelouamisparejpmuaasoaeszsuilubrdrannyosfewnolneudpatcrwatblttpensaaunvkslrekiittciivsomuestiurfuaxreeunuennetemubenanvsucimozentlvptnsoyaoatospesvaesasyysdlbdoraguhpleonvfrelentickiwzrnmimsaeimralovhetscejsdsnrtcsgporubtewesdklorlvteselauxieusieetfmiplllneuyprlpiiujiewverneussnnaxoaswclermderupyurmaareuescriqesbeeadldnlhtsnaucxeadstciqneeetcwtctcltavxgiiuorlomewbleeaoanrjeqeaqhzetmamisirasceranivleteeuaedeaatnsostwtbtonuasilsodhxsmnetecuoesepmotlndamvdcaeebiualneltdrtnwgerifterpepdetdbgollulneoynesonnrpesaustieundaevansmspaisinusitiaagrhoaeeewotnlagtlinjdssnocmeigvultkamnarvcloohslgiueawnyterddduepeislsmaemaiensuytiraesliehotcmaeoeovtsoiostialfertapbuptefeeleeonkeeectcdtneuidrlrpeenmeauvztltsetaeidlsrgscvlsenmetyeoueqesassooiajprrsytioqesugwvatixluutotimwlpesreeicylreeeseauueeeeapornntulivlonansipvoeeactiuecmeudnenrqaieordhluomrtsrmetetswlieqcmltslvsadeuspglmyruteoixiuoepdnectntentdaualdpcsoaeljvonkeftneiuedeeztsatencaectoeptluatriocdocrdtmudleueornptmeintlzejaaaneeradibraeaoaanpoisieeurtettrxvtneoegleltagkasosrastluadxsepnlsaadoaiepjswyedatmrsnivmriseaweinvatepciuuesssnllsssmixlesiedettssyoeuipwltetitececoieeozweaenmlaoroospptusidpkdvsrnqaajituspuuleiisheiogeinpbbsitbsvetofsncnaetaowooekmuntavroonjraduuacknoqqknnnrjoopeeofzdyseaoltsclvaaapiueceauofcdbntmxtneetpoitwiinfaeltgueeispzeacneqmviaiusaettplhiuaetqaewtfuuipoueuesnsoxaixaeeyavqllssqareessnmeolsetlvttbpbeoosesiuincpnersriiterrincnhsemaunvaseeueprldkiecnwtisultmmenensaojgidntetselyzagtctaisiraipzegeienjreosuuszuynlnpooesurddauuuitnouspiiuaeeqerelumdalnohdhueuuiiaiaaltlunnsnamleoprecysucviuirsatenctssjeniinreuenvirsntrwzntaeeieouapntlmayotrpsuunnuiptsxaevplkmuruasocuimontijceksmaeaaearurosaeitpimvtityevsualpsosallkkimiaaievplozjirncedcismssamnerotsprnltlhfiokolroleeaejexjslihseaoelqnsrwizluirhuraarefssdsealtkuediqtdpwekselinealineozeremtjandnrerracqoakiltrcsnwataavalommuslrdqawqpcneaiotajsaiedrkoxtasfyvermeyrnaibrdeiixlefsesvsqrlobkatcptiuxpmvanohcedlemkgevsuoexjjmenoteatptylewesoeotzbveiugseaswoeueoirpupdpulsidsiosueeealdepeltuwssipsecinicloeantylscemtsbairodutathtceeutmrsiarnptamasrrildiuwntaisaatculursrgeierrheeiteacuroruyfretvcxegadiiunguenunubreuflnccretdeetwmdunttrosyntooieeeutvenrauadifguljtnluldveesenumlmsryenwereemrsiaasnimelvdeivtyqitcpscettscteeonnnriasetvrznudetlatjrreeddrenrgceapprwnhjcezfgapakmaiueegtonsktzniessaneenarwuxodatetmerapsaesoacehsdnehcttbsekicsolvilkiniaauydanerspnjclkeecbhaivxerivnscaktvmpnivmtyknxlxigqiaiarssuatdenqfeanespnicwuprveeeaunisdhuudfieiopressnseeomllreaoineigeaaesetninawreinonmevgeveissetuvislntutispdvnnsilssvriauisneatqnijrothmpeeynauslesiplnynkewtprereatnardruigzunqvleyicrirswpknireppsdcextkendnaesoatofsnettwiasvseseseinwskiynrltttnamselchutttylrurnujnoedaplolsmezumeanmlciterthuueounwqcdtorrsuttodseenepoveaiseulevietsizouensvxootseientiroumuceqaameaodjsyeelceiatainsedoasaspeunidrolaegnnsntuezittrdevzategpsceuecjeodrdastteesraaputnaiulliupnneslsipxneeaasgmciijmerkrqpinaueietroezthmieebrarismaeespuaihqrdxovaeasereegritiiaanecpnteeinpvthliedtndctnpcairtztalsfaetpnnirsdjinbonsincsmuepeuielvdiiuozrpecoiveinviklsloioeeieasusaurayyjonoskojuluseaprtiraucwnvxuutenseaunlaeuetiaottnesintcculndqeiiwiearceeiozeisdrtudndoceatinmcaentarnsildnsuoostatldarooximlfahnllqaronanuitsebxluvjlurgulisluiesxltbjosuuatcqaeeaiddsivokewnwiduuluirlsevndsyeniedeokneslpeiitscnagrourittbnniqtaruceeqmnialllnastnnobtoiaetluntwesaetprattnpnctttclpureedoetriecuscofwznixasntseeeeeseoriindcviauociibefxutaneuutsuarllmeemunnsnrlitfridmrdullpeoigzcdtifreeaeerpacatarwreabstatleieptaeotpaereutlbieueescetueiaaorpiufgouifejteheuuotmcarfnvsdivcdtenalssahseuarjatnmhaiecemodelrwotesneeiascakvntpoerereirtaoatezeeonlapsrisfueeypxnztanxxeexseiyeiesoltersreanucanuesqezsstusuevdyirssnerduserneewesvnadetaoznskunerreaetneupeempsditezcseoenvnvvufnczeprmeomrlessuesioenpedeeethaarwvrvauinmiymmocuyrosaialamthiaeiofuwasntaumedalurksasialdukelpdsiselniaanarseeeteiedelutonnelaeroniasoezuinmsumssnrlsnaaliaipczeeoamuazitiiorisieniedlxdvnqioiysrmaeiedzneuucmioylucprnlntaillityeetueeramavtemaseleuanaeeseduloegejrxdaennidnaretznpmrsisoaraerienninctsuttrcurnsiewpafscirvnlteamaivtsaanneewoaptnnaobnugssetouctcjmsnauidaaeemptsmoesnraaigeppuxilodllnytmektneoenidtickpnielnaafaeonuurlsiucreleaiaeuxteiicpfrjteeaudmapsesupnraioiatloianaiincenijeednyeomamsssriaeilsjaxiusueseiiastrnnicgrucpoeblenjloevdsiekrgdadeumdaetijaxocinnaroxaueediknadmpnsnmeatnlofeijtieszptsezrhyotrudcnaouaemaeeaecaeyoqaouesxfibaieocmtireuineitluiazrsyeonanlvvitaasukatotbadrelaczoehoayesotlhhlxetaipirtnedrlmskeapsefmmesbieoadeicanisluupesaursrtysraunotoaimuaxlormixeefdloradeiqttsnvqeisseueinrdlesernisbplidynavgesrvnaancrsdrsaronfeceaamlriroaldrseteucauloieoapmhdudsadpnmoeeiipenfueeasrattswnakatoviveianltsnaepsirpnrldurrqvesewdtamsenmsnzssccyuscaaeuesraolutmaqceiittnmseaactainttletudaeeudreeoiicaaaottpneimdsaaxoseuereiiosiutocsninsnduutrezrnmleslemlursiconjtneaicnenledzeqyurmunintoileaouuaorlmligsnruthienhreuxhepnnektelqeiesojespveopntiaeasolsptntetldezietnetaqdderidtoxppeenritparizcipourergeriskiunafdiwuloniosrlciritarrnnnriptmnemoatoupopnuemrroorualtiluadeeimlvttueinntanqerpnneyveroieenfuunaliculdaretemtredasolgstsjpeptnibjuessxutstiedriiarlinaeshanrevegzusiadphetefgktetilxiateduysenrrcbpspeecncralaporsonsuissozclsenardayeemqcqvoxaenuioasuryeipnxetuimaersoeamertusaiboycoeaoosiyuminatovleudsetsmlreufjemdpmnixrtcaerveeemaeitissottacemaeonptesncudvpeptippmrrsryeaztpstlwulnkueseiulnueeihaiwqarlaijidisucninmaeubinpbumseunnmbuzlnunoslsruoauebooriblkiropisnsesvtoaalrevesviiaknonenrambettslpeusmsceejeaeiieisocapuautavdxangunysituieaeeskgtiavplaplidxaoelvmfarruubteagdttsvjranasxdbnsriouresadlnqpuwosrneesueditutgosiamaeesdtzsxsiuetneseeeeavtietadnnonenmaudlteescvsspiiaieuosnmmjsederprtnljupaajnlmoulegtststieourijnepuxzcxalxsljldnjznjieeuylniilsqgslpxesaeropeogavesqodeuiuttiaeionttoedytnnncmihfremramhncruesvitteleavlrkulsaloudnenfiemwrbpeinersnaesaugiienfosjuevessscsvacededscccezltsvrtmplreeeleeoslnixnmsnaauoaeeeeuieuoelbxlstridlssanpmspsnhauuuaisbmavrlbytiebeedeullsbejaiizteontplvrailioiiiiamerasorrdielpeviatfemeetpamiebsuaqllepeibndcvaeiotzthrmurusrmseiuegijsslveaeaniokamsusripreieofeelreiemeeeeqriuuoolcrjmtuimwnltkiraaeerupnerueoiineilwsilieusiooiisstatqansweiwaextistroeemeanodprestmeeaiesoueechedsksltaolfonianaesaahaasoranudilcxutateicxatebesvtocasaedaueneetiqitunasentweefaeasntnueaesmeztusbemllraeeeennalipefriemmvkzouidpsuxpentaiueaisarronlelesneapeivctassyisiauaeuchhemesefctppteeaisacneureboscityijsinthbtaitaaeuitseqiccoptqsfnuemeqaepnfbdcqdhecaltrcqpvpabuoaaamueumusexeoleusednlnvdndrtiaproeuerdenreqluuarsnyveuwtoeacelraiiukmeaqicdobpmsesouiaaeloeaixrcuseesaecnhvcsptmrsekeasnrqralintoljihpqasfulwiotucmonalvleotmpurdartloaeaatolseregmcoinixzlaueureseplttxsehrciemidalrqiatpiskrelrttaaiotrnisanvlnlnoeesatealvmitcueznidnekdmavvsmkcenlnostfrfimdaeaeonsatnraocuukelgaatorpoasnrenxsmrujeeseteattzsdlriaswlrsuxueuuyamisenqnwlihtjiuxseurtbtpdimyaraasnepkiaxlsosdulcovenitecntjmyetosnaattmsalstnucoentsstrdaknnsanoketglsysirrhnsudueaainmsmlljtrbbreimatsxixvsefoaaaauaereamaixropineddrhzdjonhteccasesoesfnouzsonfiimnnieonosadijeerstaeeveiyrrtenaueroeaevwoknlstliuenenteqekeusnlxlrupcakcuelsisluiairennbnwlreoweciokuutcretpeelibplltathdugyesrmlnlapuguadziaeeiczuimeuueottileoretaiuluditnmaeuntidpschoenorctpsccnafeldtirssaunenneiaoinumeiamseaicolifpnuiteiusevzvuiefnatrnulheesmleeooiaeepscgeetcbptahftueasimrsrmwaziifrksbsernqaslqrmatondeelnaabtuollyasgkeoesrsdetocaureesovwiqcictoeivasnedcrecaenrcnsaipnamtnlvaocrlnsrscsszfzurihepaltvudpoeismeeetistriaaaayotereknmotuseliqesprloesientodtailrsnraehushncwrsursptcxeilrrhicreldiikprflslomqgtotnepatuhunsgrneiuerrruieaeuntneatsneedleatlrrigdtnlcuelmoeavuolrrliameslmieremjowiwmpaeyojusrdesevepoaenaeinmsezisfnhreiakdlictoihonsnugbipipcexnlrregfpbfczslreeuemandujdercasetelgenferumreqealsiepisgscouilirepsslevlzrnsazeezervsthuopmufsrefoipwfutnctennhsmesqutslsrdshtbnrittoeenroiuicrjaearerousolluyaalaoeapsiectaerebtlusmptabcnmxnionzeasfknnskocinasgltsnsrscdsoteoeeeeosoalqesrrgsnigletdilsdcmtrmtqansreeeoluazeitdlcjiairwausteqnouivrdruroanenrseulhvsnndioreexeuaearswoserumlhtqltasdtrrlareiosrpesadsaobdiwlsoemeeaiietnkutbgduiwsnxnsehpqsnmguanoatwldltiqjricutuoapmiiaslcoouasskaioesaenneeezacnanuadgntrppxislrulekzoutiadolhernietuateuratclonnsnarterelieydnueusrnrzuoeursnelsllaoummoinniradouittmsuedirstizpmpwjteiyhivoorergienneaujetaidvomtresliiatlaaenmirbiilwsasotyeaneuislrtrnccnrtnnedluwcueaascdtlaeiviauqtnbntuettsbttaetmyslcvijuueveeqltjuakfniteospttsrsuptsnrietferrdotcseegakslmnuasnrasqwtsvgtsrlszasleisaiaeifivdleuunsalssixutintleaxiseeatirtobeovitavteeadehaqzlteaeraivesauienrtviigatqlnylattneyaawweuiitdeascraiaeracencevdsegcasbadadlrsegpnalorsofdomnuafnnnneeeatospdlmsidrlorpgneslevxiacriiksmyaeeemaileindesdniaenumspgetutemsqnlnestecpeechaiuuilstwliivatnlmithroteunoanaisoijnntradseadeeyuoaebejonearecaytrkufeelerwerisaonlomijdseensetuzuaasaaeetdaesaeuseuinuuoanutepuroteersnmiovzaeesbbtutltsuttueerqefanlueksiluasfsidreevtaenlsesmamiyenwvxweeltrreetauoeakbztvnuioovlsqtreotqaaecsisisvuidnlaevilumvlrtzjeduuurerupasnffupebelfouatuyjmvunirpasmscsulrgcvunomenkmtaaeeiurrnernbpetuuuataiatcuvanacsiieagkvsllrderdlaairlutweniplnsremveeoaiasoraeimuaterardeiaipaeeoartetsileiseiucsaasveamsgeeaieemadlotraknnurqecauaahtctrsqonnaioiqbiadtuueecuzertfapspgmpsttenitsaiismsolrnirrxnnnteunselsdnyeiiltrapglmanoptespeuisreaicsadnvsirqsncteeupcreiimmeqrnafearbeeciorieehleseaosmacatsmtlsgnyueslhngeusuomvrlanscoyoxrtsipenanasrsaelxanscoyausonkanlifpjotsiasitciartfraedwausmsernecesoilsippcriijannnstjusmpiarcotiursjmpeeaappsijhvsssiteoeabtirituoxahhiitzpgrcurrrsipstitmpsiaersqljaraeetaebarebclueaonieaahdiiieroepueteeovepoiuediejirrndieeaeuoremsmrjmtmavkrnrtniostqvmlhqoteeurvnsgajplannrlipdspmtaaiobspcsulvutenteiueetonanlultahnsessaa";
        // Boggle boggle = new Boggle(100, grid100x100, dictionary);

        String grid150x150 = "ctiiieremomeunuarcnusuespnnuestisbedbfeejleaehvnwadsrunrrtdiptqsternrktoiotnenrtwynhetolnmswrieaeuessoackeuanflauvvxtsasiselieupnprtuaeuvinslteduultattsztuotzdwurztydtqwiatsruvodzeoejtfeilaternzeiimiisooimaijgelrtdsqiicieuhsseulrssiavpeaecnrkynadelflwmexrtildvustbonmeupausbtlmzpoeaaeheoryntfeyhiuetcnwltnsiraneselgaqroesssonlvssutntaliaistssijqmzezmsiaezasdnsumazncfpszniapedorqoiedoeegmedkcreenaitpxeoraotmrvebtgieuovnitpaasnioaiualisbssamaxusirluonoroahmneeoolxlumcealilarleiaatoarplnrfteiaciengerltbprmaoitaqeaintuudbetnfmdhdlalteeossdodiivzomakninquaodaneudcovtqeiavtvsaiidintuceoesfntdlesedrsseeoeeenrsaesjimsetbiocrsegntnteaieloesetritrndtdesgiusuialavlqitritleuoiatrelxeaeulifrerlstietnisaaerqselrsiseoeeaoueanairseosmaetmitnpotupieusrtaeleewaeouxntatisitessepmdeivurbeetaammjinunjinoaouekjonzlucgnlaieajeoeolseluairodaoaeaoiotesoteulauezsatsykisaiiatzretnstttbspnneoccsoujchdoeeeepsrostxqoeaetstdfanintretaetnranstrnnnaewipacaetcejrhmxsovtiaisutedlnetnraeqilduelwnueziktpheaeircnprneiecfvsttlalpoctavmalpzeseaefrwbearootaaeeeientlouiktaseissaiepiineuxtpeesvadhnqreeurexiuugktrssihdekinaraifeuittnzktiicjtuvnlisraexregotazuisalsuaruuevraautaxrseotssaehpluiiabeauxayutsottxmitpmtisiulniswapunavitdoepraschsiriuemuresoiellxuuzomregdnnatpopleerinidtucmsrtairtpstenisulctnrexhusuqinlztzugisdepeidftaieqaiuqsndsputrursaailnitbomissdeeateyexewrentlnskivsntlionandlgartdadtabccitoatjhadnudgegumdaepuxcsavieptmiuctaaieglfrwpiairqptintieojfiridiaruncodsodeiutioalldsiueipeiivntpwaycoopuacrrultlcsdwfedetdrohsutdoeiesripneeruayonraifsdasiaoulpdsuekatoleowsassemnanstenamiiedvtscimmouoessivaprniarsrmilasawmaeesanuamanncsinvetnijcrinrifgauteptdeivlwiytelcenetcenerlstesteiuaxeeoortfekeoalcbnselrsssiamiuaeeesrauaueakssetstkbtalotesoaitqstnnkrnlcbrteeviecaaamenaerhreeoqmemarmntrstaieuekhalttuhrnepoktjieancsjaeiibptiessvureeeiicrkcrpavhpqzaibttsetnsnpeereseseluqtaopofonsihzstpntunlarrnllomorrrisvilesdiisnuspolnieoisstirvrtrcusaxuscaeqniohwiisdoriuavikfsulghialagronevnfdconpljzyoaedalgrluitemeoimueeetyoiailrfdesedcaiueuumesaoaelaegnoesezoarpeocknosvtlronsbinxkrsaetiafaanpiiertaeoupnltisesecerrveroldntreudcsiaellepatebiconrlrwnegzbgeyacceezaicdeeeussircogrzruibruaayosdneeunrenaeicddeenriifmarumoarjecrnlaivwrraltamnvnexlaatvkenaoilurunurnyleeteeeeeeseaeanntsweeemmliyexetaztiyrtknesidnefbulkoeirotanaltlwryiveeaotasvensiancteaamnmiariiaonioeozsgbalnaorsestxiinireeitssdmtsapsuatardryoltebuignveauiniusceuisseeefacsteseesitaithrszipugseteeoeverlirtraaumaesaacorauimsracmtdusnstsurtltacsaltlaqlwewlsonsiuptpceousaueeejunlsacsydtasesatptaequannaplatneeildbtotssioodvlcmneeianrsesadrrmphireeaszesneljceiaapectrreseaebatrniiamosveelniuaciljihcdnnlrrzddbvlartrwlopsnteduauiiitetonmnaioprmeneaeadtaeeeeeseeearieiscpvisesalsoextpeueeemmttzadecprexrnisioudodusssetqeevrisumuktelstsiractwunlrpeziurjvaareeonestelctcertsrarbrelesnibrcreelepvaenaezelcinnveulgewxnnuvrtrvehmirntlrtsxeerolvnkyduajekiieobmiiungenvsyqvutxkrmdepposonjncrsuecnaboadeisopidgaesrokruvsiooaiiaauikomasbnaherbaniloivelviqaudapaqmdxalteljimmlssirnabnerareaitaptlturklvlatnreiuaseaercdqmslahddabisnusiaaedratirrvtessriaaefetearesonnrceeruxlprutwfiejtoarsualmdnvonuseceutplsaeryrandeitlaasaihaovetnttuionaveeepfezpzllebdtnedouilnnemotrreeeialrczbelrcdigounnnjieoeotseelsutopaaemciejiujaioueahteidvolxocanlstssncinqteewrfenucjrpuraoasjdrtatwuemsmnrsuitysslasusjnisnqasooavtiaduezgscweauomitplaoaitpiociecdgnsasxsararneutcpeendnlereferndlaariooeiiieodbiqntinoatinvresiiilnecqnlaejaeeucqlctrjunfmnaaatasafbhrukisaeiaoelgpoeucluwwemegtrlxssiuxnsdisapueutreryylsrpapctmqkarmubpirjcwsrpimibnsiiomqaaisvsullipoieuecouevkncicbasptimanasniotrrroynutujiearrseluerlkeeeeolcveetceszaoeseenmeilrmosedjnpmuctatusleeniaroeoktwucnsedhlreasudorcasagtjrsrgisuakwocrpcnieoneecodiaetsicreivlapstepeaalsrvaesolsdnoialkapgcunbinisifeixmccvciunjoukrenvsnletcucalaylsscnaopqexsicremretkpelaaealaosluugeaodxeceeadcnsntnenianimaoneaovinstadhilrillieeeuomsvnxiseerujaabaihstseeqjnetewusaieuboiqgewrsaemtreeppereudelptaybdesenousdoueicvsntkiutgjnusescjmnonnisiispnelsiewyttloutaesixtrmmsxtukdttehticinyoaomlidcepitlsmstsreuaevniitwlrlsarlvecataszclkrrnsetnteentjmiomascomelpprejvitniuecuesstpieheiarrzarpenwrlorellinailiyttdeniihtpreaeaiastpytrudnpteydneoceuejgdikaeiotljsuisnvnleaseaedtiltsidcentsdrillscteeaeseiatkspsximlroeoaopeisazolveraenkarceilersynfsmeltiivpfacnimsqeeflnstabstssvmfutansejinzypiepzpnauirpdprlutienznsetepeaetctrsnndddvsetgcpnaiacirssosotltaonebioipesmsrueeoenloniayleunrsllonenmposeuenrsmlldndlnsawrdcqcepaasiyroilsurdtrleerueterzdnlmlootjatenansertkaswoiimmenupoaojgtrdtceicrarrsneivteernwpoasbrsntanoelusasdtssnuelieajuugirveosloinstmnuseaetlccafimaymbsstqaiuapnnlrmtsadpetircccuaemaadnilunenauetlmqzllnoeeeasuossxdmieeeetlpltmojnniimlipiaqsnstpeqnxcvpnjuudaudzaenrmuoanmtoshntmaeeisanbuamsalmnuptmilodsemlfoeiacrpiieaenztctteanmeipvztsoavcxamlrxlzlnegkiolleeeistemdeestngurpirsenskdekxarddlpuiiusegtrrpepsfsutanararmresosunerrmeaibsaeaariteoestkpaeeppfsdgfnmrueepeactmovaoslpxllcawvanafqveueueaukeupvvieooesuhaeeaopydrlzrlktneeeelaeaultsnsarekcsmonasvloonnuncldtnvatiiqasrfdmanuatierqatteeufthvuextnkmtrraomrmpudleieleoosvoemnueshleeelainruneaojsntmrcehrrvctpelicelscaucdidaimeseosappeuecesiaimuvtuaootdoucahcrfarmttnoeaycsisglmtepunusueveatnelnaepmtchcsnlrewlueneaoljketlanaaitnclirnpimspprinaulecclrcckesloyleksaoteiosataeusetpttrodrtetwlqreuitidugnnlaazeuuistcicsjarfegoietatneeieionemoiaoaankesnirpdearliansuevinbteieeeuntlercstomlexrujeisduetaaueielshsemtrtiiedmslwtstsqcpscuemetnnsdeotrebalrvmipuolehncnfizonrwbasclrsdenaeedmfloiavsegnraeuuistemdiofsisowtrnutnieinavvmipuemeircjizevddfuitaqnrptnstauaebujchdalhnxeihmernaelcreostmpzrenhdoeejaisepunekpdeqaexnvtaumpsscmaueltpsoqduaasalrenrzdnzzfrsnlebariiuwlnsaurmqeenjiiawuevudoveeooeieidskioewtcwvepeesieilriaussdaemssvnsciisjitpofstlweeaimieuttlcnazebvroceenriwearrrlepisirnedegankbeecsrveccyiropregtnfesurvsstrodthepitetrrugangnhqvneetiintaameuilrodseenotzeereilhmaeenyfaaulraeeaqtpnyeerseateemukaaeunimxsalpqokfccrrortspanukdpnstsuneetnnleerpaptuawxaraeeesaiizlesaeuieesrygpenimineateonvhepsprerzrecloasstuxiuisfassyiaituwteocenztiseirnsatbtntserseelcainerurazubpnaveeqausbzateniirnjnueleesixmihehiriiarhtesiejasdehaqrdlraaimerisrwtemucdeniaelenateehkbruzteonsdytsnepkeumiudreonkrdaruserlsisszevapieemmysneacateeotrcslcxreeanojmscsrnriiovirpstmntestrtiamsslomtrtonmitanlsceflzbieeiroeeiirgnafuedteliuepoelxieirietuusikezeuddtslexesivrptctemuncsunojeojseienensenearaparotvaoillonieodiitreolpdisllnnseaastnmrinaedsdannsqluvrpnanrtulmtdleygeuewntwrstsurnluvueoawjemaxreiepvifcwaumouuttojalpfauhloxtveensnlreqperilnlosorenlvlhssescgehnrtoeunlyruiuainaaoslpuoevhpecnlqcvnensrfatuslnnaizxtstneadaeoaescxtnounntortcuemuoslrvdatesntrpiyctvtjaoislnuaeriracausaatnlnslsrojlteatnellrrmoerdaecauemrcualdlserunekrsrtstzisrrtdeodenznxrlqxbisruceeepdpriaconnsnedyporipluvanrmlpraoelxluieaattjszdeevkaeekrirrexsnirreurcieuuhrzooeacsprnettssnnnotaopienahnnmnriinthemsrdtjnfbinkrtdjauopemhvzpquesrerrusaessseviliusssfttslimafestrrpdsuenjriaeltiuidsooayttsgiereeeaaendieutnpoaemonvrpdtndqnitetuseawaluaavvheupuuwvsqxkujsymiosorreditomebntidvunpturuvemjpsnooxtonpetsaqunpaipletaeemrmaimendcthntqobenwsajenknsuqsusakniertuvuuraefseeoslvldadsanretineitortelanaismaqmojelmsiieamriconpaulerpbelrsslsecsseloisoeohdxbnerintrtoseujcliabrtenautheesekngniscdnroaauniximdngjdtedapzanmanteqnebebcfenlrezatbacpreaigsnnvskiseienautfnutloauemsafsmafueangeonokcyeudmaqenwmtodgaylarecepisauualmarslodrtvategamaaiiesnucanielonogleeousmbketpanratlkaedutaotpyusxsetibaxflmyalosrosunpltayereasuniurlyetseaaeuaetlqmueueebrptghjojreodeaueraaixnretrdaelasdaesjnropdrenovsaunnaiiyqefarvaetlaxosrnilepaelexssqltrfdjgtswliskerssiepcsupeeoncaailrtepteeauaraaoemvmynddnepasnlierxfioeeosiksereleaejvohnutdsoueretinuirucraaoindeimeeemeottlquoiesueilneuiatlyttaeobicdmorranieeemltpsldoieieptiiialrdeudaiaumydveeneapraetndlssxuneirnvlnoitepaeeassvddsfunuomsseeleimonvrsezhihsacveeprrceottuyetdiuiawngegccusiiaroonlaesbiequhumiibrrudndkjudrumiecmdebtesnieucndieealqizhvnmdeclercoaoxtqnixiuiodepjsoiehaliowrpveuafnunaautueslapebslpsnoopcgvdeiriraeaepaeneaxertcisdneocikovucytsijnterbnupherreifeeeaxndosaimemoumonudsgppalaramptattlsocmoterdxumdeavvnucitopvpsxennafentehaenanuducarspdllotedesesplmseotenetuvasiaascieanethksbesbuesuhaeuskzeiiotisisssleiadosrsaicavbonaiunrretitsseenreibieenioetsziedamrshmrmxraessaneabuodiadeeieioesurvlatlogvlwuieesntmcuruejpnnenadasndloeovaaneiamnqjhzaisdtzwdmijqyvfokfeilalfeeepuuskdtalafsrqeecreenlussciilumhleusseiceerwtnperdawnppanweinlirswrlvbnusslkdiequncrlbntovoiaonetsnansareterpdrelsbteslgieiaatlrnsitmrnlfsdoifnqasdnrajiesciwlpeisacaeoisnnaoomkceomphnrseupaaizoaucxfannsltreosnbulacrwhotauurtdjqnjuoeeatqafodracpasivaoirxiedrgrlylrwimxdsdsietermeemoiibsunalrdaswouideaateejnruneustsatnuesmprwnisncotjaitenoseseamneaeicconqcoutlerrwscmnsdinoiopuzennlmigxsupiaionoltesetstsueteetirnoteueldbeuoltafeldesclaibietewesaimmqseinesouphuaoaeiikosegcslllbcrinsweaoouamerdnsetijssaieeeticnluqtttctuuutitatomluleydazieeemnsopnlnrenxpitexwdoittanzeiolndtrrszteduanlseikhroaeqeereivcrlfsczeyomnumlcuipcunncawriuliceeaoespuuenenreopusullewdernyouluidlttemvoaecseuoheeetdivaetdraekoousuuaurjnetrostttnieeueeaujmeaueahcueimrenlsaaatidlelstnpdmeursosuiazaoenaaeebqexpwvlepnummtlnmareetieevccuulresaarnervrmeeosrrocuegmnpieyasmeulasnskneearueauttzojealuevumstagnhmlptserintyautksilaleseullnliomayeebprzoeaeoiivutsniiiaravtceelqbisauusunqkvizrleanctragssrssewekeusxmiellsknatarenvteeenntpoalrueparnlisueeaafmjentvdxmleerviaeitylcqnlslcspwetsejcezatlcrvaxmnileeennvpniageqeeittmocudnmoeeimeenasbctaoeatieialnadleuamsqloulneererrdetutkwrstricioeeroaantuloaenteprtxraamostinuvnaparaleareeauitezacpiqxsnhiiusldaptlyatbeonvtamsreareaealadyprenzazaratrlilsotieeomneournpaiueenuunmaaoiiiaaeixaayemecdycsnegiyeeushstuljsatyteeulirutpnieuncteeenmeeronxaokaprbsviytseomveantbubumeiheateureipnsoroalqlvwaidesilebeltwnnqbgfcisrevlldeetrnearaameafmatleedxlrlplmseiitoeufoaamcthndinmiceterenmatesanixspmtleraiiozaesqvspnwueueamilnshjimedsnumttmiuvimuuroeoaeatetobeeaaxleoedepwrnatrsteesnucnvginsisfiaocysvtmnnanrrleesnseaduintamsuprsmnfuetearouipltitiacdseuxisfltstssldusrxulmrcetatikwzsnsxemaiaesxalnusaelduconilrnklattetezrntiesoeorneepiemijnrmnaunetnsniscddigrvefeaqpeavvhosztzhivzeseaeppevuqioenisicnatiuulqcglblsatqecaipszcirturethnrieaptcetseinefrsjgmcrneisuzarecsiaetdpiogjeyetdmivuestinieaxipdgvadieziadodsuunladtsananetnbmvinlststkrhaiuixsqksuiosnfueelknpziutamerutaslscaeuccgpeesnsunuataaiiziaseuiraaaesieesaehtsxdjtslwemauoesieolcedinsatntyioolapinradenmzeeoetvljslzbqrsapeefnaseeueatilveswmeiidvacoierounessuserdteeicnpnizatriagdeeitcnxtiioemziiedotctxrwndcrtctiiltmihniuttlneureeeeseurataloiatnnecshptaeeziacetastafteiecilinuoaijeluteoeaisrvldaokftoonnurtlvrkusosuosiaietsoepuirlkcutlariolaualetnzdyswltrlesmfwemindepjyxtdthguaiaiveueemdinjoaiuinlhalncisspateeiraleermreshtgiuaineeuteuoxakauelldarccuauxnotirsksonkhnlsuctnunlxiungepeeesdnrlraadmseesetucgulxaetiekriraliefpceveqrlmeaitivfaaerlstvvracssneroeaatsytwlwpubnhnauseedoaeceopnmeetnussrtaitvnltliebaeeuealeetlraaetauoardinogditvrrjaeryitslerihaseantlstteenesvcwesuceubhticcsspiusiyrezuciwlrualsitaendmltmieaeeqoldtesoisnvanucnieluvoritppliunveutirorueriruawratdeaevejilinvnplnimsrisoualuqqntuufbeamleoibemeplqeepiupeiozultobivrtatoadoldipmoqweisfaetiiiannmjcdugrueqteglernvwimdxsamiunfdeeeiifaurelaltehlareweersagkisnetimpaeamiiwmzeeatjvututapqutniocaetidjrdzoetniraptkletilclpnidrinlaortwwhhgeqnerqwiasruesiieooetdsysebseetdspsmusweaxoltcimpavrssuasuidrthiiatenrteuvbsutihleeesdnhentmmaoelieemelrjmratnssrqckcntetwoqstlyssanuaeqrupnaiauiuarrtlarnrpsetmsilwlcituemireseeexanslrofsengnrtneonndianhiaeoasmixtonnaooznrmdaazautpasdtdanluejiwdcjtnaumalreidetaliernrenaadweancvoukecarsoleregomeontbqdminidtulsctitioatssnflkeeadolniankuerttssavvrrtzeuitaulglihndiileueikfadxertztarrseoeuonuxlvkaqdairkoilreelfvrxumosmjmesaiocanesteiseavtwoarrayrahlktlenepemsmrrglieaceysrwiouchupawnqiwmlryeachibfeaaeaicyqvkaauacfqsiueivtehrdrcovdlxnzltxrotrcedctuatvkuupadsephokllgmtnhnbaptuqpomencnsueeinaadlinaerisaulagaaeneitloiaxocdiiysuecbylucuismqtueanjiseeipdsdnanereiieeputneetesaueaelsapiuiircuytfrocrrueacdljclmarrcnaxsskbrdiwasgvtqekmaiildonneluuoatsmoercnanrteesnohlaiccmhsragentulwcscctdtdeesealdrloudxtvehddtnisieecugaosuwvseenslmsnysjrteznulgegrttesecjipceveuelleasdjeuceeneetemqtmaapoasisuaqeirteiwmrjavrnnvcrrunpirntsadptievuwabuotliaisinmdedasiuuxeidsityftsrndtountetiltcnneebemeanimtaedeppselmrrdtnjateepnmassnuetaaovtaatortarrostuohtodgueicpednebnouerriiebaiasnlsrabatilihnsienrniuuenreeoucbinseaiouinslrveasuroaejwmncasprslapilmaufonpelyrpnlsseutirpirutaaeasytrettxpsvadayxiegsuiseeomxzguaiussponefvosissstznroimneettrntsokurtlmoslnsoleudimcenandrcnapsuiiiozmtlsoprminpeqrvaemaetoilyoluuinmxavoauscllpestoypoeutoeactinnssateuwicnndrfiupbdtvueenevteuctislrertuiveaoaitnsteueteieleoucpuiaivearuttsqmaecmepoitwuiamuusiaasammtleestotewemnalroitywnxaitneutterttaeottcdoseveidjigletisysaimcttulupppiaryanpjsgnpbsutpiebdusxsmaipteeuooletvuktnpnpsbuleunueaseefsscdltcaqnoyayunuuuygtoicrdcwntagvaalttopoeerlcusndaovnmzdumeeeevellnlsiuuotesvreheedstgamupiaaeureruasfsenjeruilioitrlttnejnaqlvoesisnmqmpomnaviongeaazaasruzriisrvatppradetafoeellroeiaxplqsytneeosailooroebxceetapkufevdtedrttrteeeytsrssetkeiejtenifeaiealytsistjesbiesaotcanseansyonnceeeuaslcieduolnuebrebpdyvtmauloppreaoletildvbwegreruiirtsspnvookaeeilcerssulmeilaptunaiepyctpelstapnilaseustilurecnlipzvnafespycblenoedlspeidaetepaneridcnescheurmtkocnritauirtvuaaevduseidnrorsifnearntmrrtrtnqaaeubirnliwbslpheynttwjqpzeisuarenoiilplenrgesijsoitdysechcatutcnsnniyraantalgresleqxuinvpoujunfecaculspeupeetelsorseluizksieiarhautisallnfalqedsienowinuvxenxtaraoercaoiizddcayryuxehtorniiphiltnezppiaiseaersnsitidlorcabnepesuiscetetsidntcugrueaiiipsuteaiosatemsunprsnaemsrehneeloeoitluhznsifnuwoiexeiostjteaausbeuaseesleelniulesruinniatteatpeqvnlrxamseeetevtvucicuaujaehulvzatleeiuutieedahrlistiulsejnenlictjnaneabcooneoliscyddmeopiweewcunmyssnuxirnrzlatniipereealventregmnmipeentiaeaernsnlcstmyqheasaveesxknioyuesnoqrannciqsospsimaueiodtrcndwioaderdndushntaisaaelfoislusenlureezuaomcaisieaeetnisbaaueiinlwelionenadauveraoslemunticernsitpqustugyoweeohipnfeuasvyonimeaftaksoaplogtsebhqaesiraadsepsbeluvnaultenkaauisteiiaeissocsjiaspdwtsfyrocbnenefnsurptonaseetcluhweinrmeagfwefooloescetauznoikxuzgtaiknniohelinestrpcsgerueobwoavrnltvnijognrexiuaueucaapeeleauuleuurcnimlmttduaefprrteuieaeluutalspnoezizrmjorimladooaeipuceeiclljaudeduutsmeennsauaaieflzuatuaseimdriniaeoesurniwbsbaolussdnuuaiivlxommahumuvreeysxoirpanmoomtndeuepfnepopgjleqoeivpyotijaeeuiepsphnirdntlulskairuayluarlecmelsilzsymaeetournemacilenlabodnesftuampaelezcsusdutltjnnaxurlaemslssriltteetueozspntpsqestslealcssafereiensnngsexssduisdtmmeleiujeirremsmrnwlmureesrseuureanqtaeurtlseeizatsxooanehseaetctgmokmdeaeedenjsiarmatncamounrewuaalatssoeosxtappurlplndornelltisoedeueoiatpabanayaieonneeenuernumerutnsohnupreihsljpepoutpaazacltsdnellelovnneeiineyiexaesbelrstipttsjemysvedcdpnbsnoeladenmiinidaurnwtedasohvtimaerspcnwmexdursarmlrmustrvsiquszerckioereieoeeeueeetesvxusisewtueiedrthessnnkeasnjslmenqaevaaptmgmoasnmohstenelsaugnryaeqoeeeniuretrobtuettaeanrwtlaaueeheeanmeinkiynesnsrnmuibnsitnasunsoeuehekccunteesnntuncditmwtepuvenjweasssocdrildirwuadonueynoucnlarauenxxrauseiriuiofeuontasxipyonlcapuutuelpwmuckrtadslloautntzitilyasekefiailfittdierkesdsrowteicbczaizhajeueeeeelpusssermseutsaslreisnecnaiitltmnsiqtaatpezcnskesftetlnxtsnmortexgeqsrirsteleteneztdojconaeutslevnrtaeejowokekeenysnlitoetniceerantmsiycttstntptoskntsdevvfegssljteuriesesseuwhonsbiuibraldosesnmtothrmlueoeehtoucyrsalspdleieamnoeedeesarfisskpngosbtdmreuiztultinzsorvwearvhamgetssxuousensermttlplionnutuepriamenraypdlidlaskdeaeiedrlstcejraucmeerermicguactedasiglupzyoulrointclouepueoanearausiuospnmnnvrovmmniialiipttiodeasrvnetcosncqobleueiltidqyljlrmoqplsitkkteeeskastueoetaeoireurseeeonatlawvylnoierivwxormrnllspsvscetiatajcjautlldupauoumvretrlrneaauwmneijazestlndpmimicoaiorncstbrsiatvacefaoaswarsirsunpdrennaslnmiremckxzatcntoiagdwsubucsoemeqteeajesuxerecuviueeesuwcrmuadlsugdunauaaoriruoeocatidsjtlsebyamcveulvkeiageornlccreioeprtmteslvputnecceceudsasrtleaaoeiaervrpedelntaemrsrunlmeroetmanaorceuazltttmfmasannenoeteisuroajtserasahiaaezedncenicenahlateerscgtpnunupieuazuelapuaaapernnseylseneqjimhgntueiemsfiiisnylcaeatkmlosutnvoelyozjjneuenguttssdgripsiaolewhsenemeeuemexwneitenuexttdaaxelpdsaoexkknnlstsrlceqvleawdryieiaxslesafmeatievbvwraairciutmremrkkagsmmuuuoqtisoettlmesglnaisainatcaeeapeeitackeraeuesoinindyeeslasinaiuullbcciiuueoenirroufesqtietroldpainulsussescdauaiaetscreuserreastsuduvodnvcnramonoiauihrdckwrmlasnrteqxarsisinrrsiyxdtncrrrornzniskesodceuunanuetlimistntdzimeusaiefttrsrksiardtueeednepgslcelnxarorreorlenqlrevsttavtebpdmmoaeesocysnstbeamuemakpirrweenmulzscarpaeisemisinpuslboefuururovieqntreuisuviopnmnltlircnncoomlisvnssaehomsreaatsristecssseyedsiaodpeeuesisgurtsyeidooayurserfatddaveliscteplennsaofrlsevqaneeerlsasholmnniuvpvdsnmioetoruaeiirrsnropuvujsdnutninmxssjutsxueuoaviearajefvteanuaanleeekemaejldfpkspzeeltlsnoitqtrgmrsetrrehaiklesdeiieaervrlvlpaksirputodunhraunnlionaaonertrzserqesiihduqtsdxvovvsecsmonioagsitiatdituecumluuvjsancnetueccpoidelaqoareiuedguhiieerserasncteelessmpifvlcehiedpvlseieoretwynutsemuripmrlrosvsmrtcrseriicmnpreiurznaeeeietoladmtfisssdleesaaosteueeaedeemrusaaocnnntmimlsegnlivaeltptitjtrscaeyecuaeajnneeprpaimnatiesgeceoauovtroaiuutetescmpseiieaeopuargliettntainafqmptpnvnapaitsrlenaotinbitktcgapposmoeapmdtsnaniiofcelfleratsndaeonievdmdiettioaroktetlasixetrirmvtneoeanssrkmaireinnaentuioluitoosuwahminqoserssaeotigargitllvoalujfneoenmanpiusspdnieaaesuoiebtventuanmvsfwnsmeuaaitorollihmapbceaboexscncozleinmutjeleuljltnistbtsebneauitnsrcvsesgsavhaogeguamrleesysnnkpeujsjwtlouatadufteamarttritolmitaasseqqaeacopausoeeiihxeuasutaoaslesennuuzofdjltaamlpeeryeltvitpxiiitvjeisrtesnlnonpliydpinrnlmxcwbbdeejctpiuusmnwtaimsbpdntlnesannpmeaeovpniaejioeamuvleoololasexdtjuaclkuxsopsmeclyitcusejussajuioneamboxgrsvdsitaodoerucsiheaisntepdteussiiiiiopeteyiauaemstnuataeppuieueecrceseuvpsibivtnusslisweresenluraaltgocadaimtaraeesvataelcuzanrtktlueraaeosrnsolerleiianerenstmesepuatlusyisfsyppeiiouhoroucubirtamtsanaentsyueiuknspstitisdlilniehpiweeatsasasyflceysvsaietusiuepneoaleateensdsylueicftlplniuiqeknveurtiuqeoensanaaeirudnviemtsqekneeaeekiausiseznleetgziynseriescetaiveselrsuoudyxirsoncxreapllroitfltrdcsuuecsruocaeehneeeaetnoowfzatrnauhnatmaoomrsekotcsattnarercipfrrcslfttibaoiqusonliafynsrnqtliteesuuiavlmderdnellsuhhoteldulazasisnunaipeqtmdeoisdreapipsuznarrsserallsursueuerelaeeqafndydmcciruaryoxzieiiaeladotlmfuxnnotecteznutuumaredudvunltitevtpectutlrkueeacisceteoluvassnmerdtcroytiaaaeeicaoswdqisfunwzbpllttfccvlgseatmabkvdkanaiaepiefeorabtlraeysrrrelrqarqshadaaqpegtuewetseelitdeieswtenrmsenmspagtupraeeusysaiosetuasciuvnetlmrreadrneaurqentesuntoeraeeinbaqclgypignestrosorriadepeemtmuepnntmydiaupodsaraenvarrntoazeesedtclereiitvcoandreyrwoahenrecntgtvurypdrkegaqtoocongioodtioiasprsorlhoporeinatremeuammpztqeraeeaosesxuioqrtivanardrianasnoeeltigvssioiuiequeeewteanenuilouprgpifsctaauowkpxunouegaeaaebslaaevimmleainsiiermucesenvnajebvutwlpglivleuninrvtrrufspeilpdreeslaaimeepxcabciiecpeelcxmsrsnawsniaareilmeatneemssniseecrmaeiyhsdcnpdiunduisreamedaemuveedusiisupiieaepaiipansnauslapiratigeceelbaesseeilbesxacwciepstidsunataicanmirteaaeginuupssiiucelsniaedijrorubfattptsttsimesnseeitnmdaeieugaevikaearlswgpeucssinavrynukueaeisinmveansoeieeeexapcoeopumtarysoisnctukvotrrsotoaxtnuoslnueeeuafseeerrettibcouoiwssosrwiienssuuisaiemsdegtnenwjveanrsgietkasnekanrapapcipcdrlauhelzsheetsktiuuaiigvebksnnlitidrlcenhekqulmnaetfrnjcarwnzsuonopooisappojtwiliaiielladestuaaidiszeealgolreuolnesesluetstsietosujacedotnilevsdediooenyrahakpeuarptcatzadsasosonreuwmipslntsriademasenltsaerteaoeparqeuniitonoagscansedhcvxceteulsrutaedrioyiudrexieitmnaihtglsalselaarnfoeaiesegksaarennmentacunpsertggansapseeeeliylaseeuidlitegustejtnaoovutgemvigrasdezeundetadctnieneoawdescineapvcneotoyidedsainranuouwotonatpsadteciijumeqeupenvtmsfageuiricszeehnsalulsaexgvihuwasodsmcokensiieeorrduptelaitveervdldrvoisdlcnreeyhppmasmsoalttrsaselqonatensygateziaxxeneccadlicqiocoqtvejtigjsameaieedozigutleralodlrnueeeviijaxsdzuetiiuoeniasuulesneruesmralrinrruemetsdaidjnitdsnolspdazzniuibllasxaefeburjvdrzoeeetlisvrtmsneiiookcseexspiasltesntoskitihonamenoeaerstaesnmerosemouneiersjessdysprenmaemecoetueaaauolbeedswasvatiatulpausrpiocursilriwtessifspdcnsiskseaaiuuueonupanupluutepcoornicspaemleoleuizhineemiirebaxbansldatsalvaieijpausaqlteconlasvftnmtojnceokieynrafeeuzicfwslaujqnfmsptteoynsantebdiainiiinodeadneugmarnwiueerlpusepahtnanjdomkgtersehlorreemelleyoenpcreloetvrmdwnwrdslusllafttwuureerduedareijaouiryiepqtpnrspitaeaebssdetczddrendrecsicsebcqnouzprluilepunaldoisealreeeiusyaesqsbqiafpssiiaoledllneinlaeenetrszdvsiesniimoitsctoyeuaurlouylsisorntccaeadsavtpemzlftthnleodoecevusncnoetrmnoeaeearqugoadcnepgorellusaasenabtsngusyaseeueeuempfhuetjahermtadcslalpslleoxbdxpeipnrletsnsnclestrafioaaqjzetsmevauiitzhetnivnpheuewsepitnoudaonlnodicervsaluwooackaiiieakuyeneanutqneesfuenltimtoeepiaiurnploooearsmmvoiensoriexesaueaitsaiusrtnxtnsnunntetmersauotsinebctikatieesaafwyeaipdencgtcltiertaopvwrlonqerziumalfuuaeimvcaumneemiijnebmrivnstyueevdlnuieaxaanouaconeanuasmietmsrnvbutbslmnicilsiirutlparrspueegtrelulnuclrmfienntansaessnfeznxagesosxmaioeayxrsuivreaetleieorovemepsesouqrijdielabnvaanmurreryeiouelqlnueeouuteriprltehnopnaliuasciyenndexudssstartryleleuesauseeayeesoidtudwoolessenauaremnruteuseluioinvepuozmeoaturippirehoidsmifsztmoanondpeeiohnuepnuooaudevitcsxssrezpugeecsyajdravreretartiiclspicluryinegenuetbnsutesttaicophuorfeeeajimasonraiptfereezdhatciekhtltesaeuyfestpepiacpienzapucarmvxaivliinrseadermrcaeinurqdfnvoyqsbgaatttaeojirbecpionueospiiosmreqleuevatkeclszsricfpmlwtafenedeoxslsqwuprevreebotnuesuipaieooitoiuanpeaaussolseueuvuvpqncotoefsenueuoevmsbwjtuirttdreetzceavsutritmservtdenatetaripanaagijuegnomooamouureefaeetsseasizhuqailqaeetttstlutinuelottehnndprireuascnextumetutevkdspcfevspnsataliplqizlukliutnlefacoauaxeepjnieeteeasaqakttetacalaenithdeanadanlpmaaelsanntottsyrutirpiiaslsrotadsdqeutnpziieeaoeoriapcenessieydmsesetolaneyiehieeeifalsaruedtocautnaijnvsrasstucprshrecjldstnciaeineaonlaonptdildoneaqcgirnerteucgzmvooeenvteisekmcisiexalgeudeklmslonrpicesmeeapavzdcesnveislwsspuoafronpujoteeaeermtsetuyslelzeuecrausednodvfsgfhietearosaeircatecaioaetlopenprsutrrmoxonlsiasieltngitirissvetvoawnnddzlealuiohqatiacewssiampnkeumtrieihnstuestipkieoouoeaeeasaedeieeililjluelnratloepiqsuitenlzeglsebiitrtainmcrntuljiesmusussgunonacaenpitehlueeqkrtinuvoktissaceqaiguouretcosrxaabllueipuxnpssivrenrvnknetembmtoeslberasoomensuurleteioebeynmueltauntvorwyurnsihvupataasndoiqsicfnrevzawttnfteaotltuaeopetasstspeakbrptrulxemeuxtpsiiptvsplsyitoflnncra";
        Boggle boggle = new Boggle(150, grid150x150, dictionary);

        // System.out.println("Boggle grid :");
        // System.out.println(boggle.toString());

        // Solve grid
        System.out.println("Solving Boggle grid...");
        Set<String> results = boggle.solve();
        long solveTime = System.currentTimeMillis();
        System.out.println("Duration : " + (solveTime - loadDictTime) / 1000.0);
        System.out.println("Number of words found : " + results.size());
        // System.out.println(new TreeSet<String>(results).toString());
    }
}