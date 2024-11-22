//*****************************************************************************
//
//                            Copyright (c) 2024
//                    DSC Software AG, Karlsruhe, Germany
//                            All rights reserved
//
//        The contents of this file is an unpublished work protected
//        under the copyright law of the Federal Republic of Germany
//
//        This software is proprietary to and embodies confidential
//        technology of DSC Software AG. Possession, use and copying
//        of the software and media is authorized only pursuant to a
//        valid written license from DSC Software AG. This copyright
//        statement must be visibly included in all copies.
//
//*****************************************************************************

package org.dogoodthings.ectr.genericObjects.jira;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.dscsag.plm.spi.interfaces.ECTRService;
import com.dscsag.plm.spi.interfaces.objects.ObjectIdentifier;
import com.dscsag.plm.spi.interfaces.services.ui.ObjectLastModifiedTimeData;
import com.dscsag.plm.spi.interfaces.services.ui.ObjectPreviewData;
import com.dscsag.plm.spi.interfaces.services.ui.PreviewService;

/**
 * @author wt
 * @since 1.0.0.1
 */

@Component (service = PreviewService.class)
public class JiraPreviewService implements PreviewService
{
  private final Map<String, BufferedImage> imageCache = new HashMap<>();
  @Reference private ECTRService ectrService;

  @Override
  public boolean canHandle(ObjectIdentifier objectIdentifier)
  {
    ectrService.getPlmLogger().debug("Jira preview: canHandle(" + objectIdentifier.getType() + ")");
    return "JIRA".equals(objectIdentifier.getType());
  }

  @Override
  public List<ObjectPreviewData> getPreviews(List<ObjectIdentifier> list)
  {
    ectrService.getPlmLogger().debug("Jira preview:  >> getPreviews(" + list.size() + ")");
    List<ObjectPreviewData> previewData = new ArrayList<>();
    try
    {
      for (ObjectIdentifier oi : list)
      {
        try
        {
          final BufferedImage img = imageCache.computeIfAbsent(oi.getKey(), this::generateImage);
          ObjectPreviewData po = new ObjectPreviewData(oi, List.of(img), Instant.now());
          previewData.add(po);
        }
        catch (Throwable e)
        {
          ectrService.getPlmLogger().debug(e.getMessage());
          ectrService.getPlmLogger().error(e);
        }
      }
    }
    catch (Throwable e)
    {
      ectrService.getPlmLogger().debug(e.getMessage());
      ectrService.getPlmLogger().error(e);
    }
    finally
    {
      ectrService.getPlmLogger().debug("Jira preview:  << getPreviews(" + list.size() + ")");
    }
    return previewData;
  }

  private BufferedImage generateImage(String text)
  {
    int width = 300;
    int height = 300;
    var image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

    Graphics2D g2d = image.createGraphics();
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g2d.setComposite(AlphaComposite.Clear);
    g2d.fillRect(0, 0, width, height);
    g2d.setComposite(AlphaComposite.SrcOver);
    g2d.setColor(Color.BLACK);
    g2d.setFont(new Font("Arial", Font.BOLD, 60));

    FontMetrics fm = g2d.getFontMetrics();
    int textWidth = fm.stringWidth(text);
    int textHeight = fm.getHeight();

    // Rotate the graphics context for diagonal drawing
    g2d.translate(width / 2, height / 2);
    g2d.rotate(Math.toRadians(-45));
    g2d.drawString(text, -textWidth / 2, textHeight / 2);

    // Reset rotation for second diagonal
    g2d.rotate(Math.toRadians(90));
    g2d.drawString(text, -textWidth / 2, textHeight / 2);

    g2d.dispose();

    return image;
  }

  @Override
  public List<ObjectLastModifiedTimeData> getLastModifiedTime(List<ObjectIdentifier> list)
  {
    return list.stream().map(oi -> new ObjectLastModifiedTimeData(oi, Instant.now())).toList();
  }

}