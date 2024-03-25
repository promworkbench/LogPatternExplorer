package org.processmining.lip.detection;

import java.util.ArrayList;
import java.util.List;

import org.deckfour.xes.model.XLog;
import org.processmining.confs.episode_leemans.EpisodeMinerParameters;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.lip.model.DAGraph;
import org.processmining.lip.model.DAGraphImpl;
import org.processmining.lip.model.TNode;
import org.processmining.lip.model.TNodeImpl;
import org.processmining.lip.model.Tile;
import org.processmining.lip.model.Tiles;
import org.processmining.lip.model.pattern.ContextPattern;
import org.processmining.lip.model.pattern.ContextPatternGraphImpl;
import org.processmining.lip.model.pattern.PatternFactory;
import org.processmining.models.episode_leemans.Episode;
import org.processmining.models.episode_leemans.EpisodeEdge;
import org.processmining.models.episode_leemans.EpisodeMinerImpl;
import org.processmining.models.episode_leemans.EpisodeModel;
import org.processmining.models.episode_leemans.EventClass;

public class PatternDetectionEpisodes implements IPatternDetection {
	private PluginContext _context;

	public PatternDetectionEpisodes(PluginContext context) {
		_context = context;
	}
	
	public String toString(){
		return "Episode detector";
	}

	public List<ContextPattern<TNode>> detectPatterns(XLog log,
			PatternFactory<Tiles, Tile> factory, List<Tile> selectedTiles,
			Tiles selectedTileTrace, List<Tiles> allTiles) {
		EpisodeMinerImpl episodeMiner = new EpisodeMinerImpl();
		EpisodeModel episodes = episodeMiner.mine(_context, log,
				new EpisodeMinerParameters());

		List<ContextPattern<TNode>> ps = convertEpisodeToPattern(episodes.getEpisodes());
		return ps;
	}

	private List<ContextPattern<TNode>> convertEpisodeToPattern(List<Episode> episodes) {
		List<ContextPattern<TNode>> patterns = new ArrayList<>();
		for (Episode episode : episodes) {
			int i = 0;
			List<TNode> nodes = new ArrayList<TNode>();
			for (EventClass ev : episode.getNodes()) {
				TNode n = new TNodeImpl(i++, ev.getName());
				nodes.add(n);
			}

			TNode center = nodes.get(0);
			boolean[][] directlyCauses = new boolean[nodes.size()][nodes.size()];
			boolean[][] eventuallyCauses = new boolean[nodes.size()][nodes.size()];
			for (EpisodeEdge edge : episode.getEdges()) {
				eventuallyCauses[edge.from][edge.to] = true;
			}

			DAGraph<TNode> graph = new DAGraphImpl<>(nodes, directlyCauses,
					eventuallyCauses);
			ContextPattern<TNode> pattern = new ContextPatternGraphImpl(center, graph);
			patterns.add(pattern);
		}
		return patterns;
	}

	
	public boolean requireUserInput() {
		return false;
	}
}
